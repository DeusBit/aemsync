package org.aemsync.core.service.aemsync.impl

import com.google.gson.Gson
import kotlinx.coroutines.experimental.*
import org.aemsync.core.api.log.ConsoleLogger
import org.aemsync.core.api.log.ILogger
import org.aemsync.core.api.model.*
import org.aemsync.core.api.service.AemConnectResult
import org.aemsync.core.api.service.IAemSimpleOperations
import org.aemsync.core.api.service.IAemSyncProgressTracker
import org.aemsync.core.api.service.IAemSyncService
import org.aemsync.core.api.storage.IAemRepository
import org.aemsync.core.http.Session
import org.aemsync.core.http.connector.AemBaseConnector
import org.aemsync.core.http.connector.IAemConnector
import org.aemsync.core.http.response.BundlesInfoResponse
import org.aemsync.core.http.response.OsgiComponentsResponse
import org.aemsync.core.http.response.OsgiServicesResponse
import org.aemsync.core.service.AemSyncServiceConfiguration
import org.aemsync.core.service.aemsync.progress.AemSyncTrackerWrapper
import org.aemsync.core.storage.InMemoryAemRepository
import org.aemsync.core.util.session
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl.asyncHttpClient
import org.asynchttpclient.Dsl.config
import org.asynchttpclient.Response
import org.asynchttpclient.filter.ThrottleRequestFilter
import java.util.Date
import kotlin.collections.ArrayList


/**
 * Xodus backed aem sync service.
 *
 * @author Dmytro Primshyts
 */
@Deprecated("Use [AemLazySyncService]")
class AemSyncService private constructor(
    private val aemRepository: IAemRepository,
    private val log: ILogger = ConsoleLogger(),
    private val gson: Gson = Gson(),
    override val client: AsyncHttpClient = asyncHttpClient(config()
        .setRequestTimeout(200_000)
        .addRequestFilter(ThrottleRequestFilter(200))
    )

) : IAemConnector by AemBaseConnector(client),
    IAemSimpleOperations by AemSimpleOperations(aemRepository),
    IAemSyncService {
  override fun connect(instance: AemInstance): Deferred<AemConnectResult> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun components(instance: AemInstance, syncPoint: SyncPoint): List<OsgiComponentInfo> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun destroy() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun instances(): List<AemInstance> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun shutdown() {
    log.info { "Shutting down..." }
    aemRepository.close()
    client.close()
  }

  override fun sync(instance: AemInstance,
                    aemSyncProgressTracker: IAemSyncProgressTracker) = async {
    val startTime = System.currentTimeMillis()

    val session = authenticate(instance).await()

    val bundlesInfo = requestBundles(instance, session).await()
        ?: throw Exception()
    val servicesInfo = requestServices(instance, session).await()
        ?: throw Exception()
    val componentsInfo = requestComponents(instance, session).await()
        ?: throw Exception()

    val progress: AemSyncTrackerWrapper = trackerWrapper(
        bundlesInfo,
        servicesInfo,
        componentsInfo,
        aemSyncProgressTracker
    )

    val result = SyncPoint(
        Date().toString(),
        instance
    )

    val componentsPersistJob = syncComponents(componentsInfo, instance, session, progress, result)

    val servicePersistJob = syncServices(servicesInfo, instance, session, progress, result)

    val bundlesPersistJob = syncBundles(bundlesInfo, instance, session, progress, result)

    servicePersistJob.join()
    bundlesPersistJob.join()
    componentsPersistJob.join()
    progress.done(result)

    val endTime = System.currentTimeMillis()

    log.info { "Synchronization completed in ${endTime - startTime}ms" }

    result
  }

  private fun syncBundles(bundlesInfo: BundlesInfoResponse, instance: AemInstance, session: Session, progress: AemSyncTrackerWrapper, result: SyncPoint): Job {
    val bundles = runBlocking {
      log.info { "Requesting ${bundlesInfo.data.size} bundles" }
      val bundlesResponses: MutableCollection<Response> = ArrayList()
      bundlesInfo.data.map { bundleInfo ->
        val bundleUrl = "${instance.address}/system/console/bundles/${bundleInfo.id}.json"
        log.debug {
          "Requesting bundle: ${bundleInfo.id}, url: $bundleUrl"
        }
        client.prepareGet(bundleUrl)
            .session(session)
            .execute()
      }.mapTo(bundlesResponses) {
        progress.progress()
        it.get()
      }
      bundlesResponses
    }

    val bundlesPersistJob = launch {
      val bundleModels = bundles.mapNotNull {
        log.debug {
          "Parsing ${it.remoteAddress}\n${it.responseBody}"
        }

        try {
          gson.fromJson(it.responseBody, BundlesInfoResponse::class.java)
              ?.data?.firstOrNull()
        } catch (ex: Exception) {
          log.error {
            "Unable to parse:\n${it.remoteAddress}\n${it.responseBody}\nError: $ex"
          }
          null
        }
      }

      bundleModels.forEach { bundleModel ->
        log.info {
          "Persisting bundle: $bundleModel"
        }
        aemRepository.addBundle(instance, result, bundleModel)

        progress.progress()
      }
    }
    return bundlesPersistJob
  }

  private fun syncComponents(componentsInfo: OsgiComponentsResponse, instance: AemInstance, session: Session, progress: AemSyncTrackerWrapper, result: SyncPoint): Job {
    val components = runBlocking {
      log.info { "Requesting ${componentsInfo.data.size} components" }
      val componentResponses: MutableCollection<Response> = ArrayList()
      componentsInfo.data.map { componentInfo ->
        val componentUrl = "${instance.address}/system/console/components/${componentInfo.id}.json"
        log.debug {
          "Requesting component: ${componentInfo.id}, url: $componentUrl"
        }
        client.prepareGet(componentUrl)
            .session(session)
            .execute()
      }.mapTo(componentResponses) {
        progress.progress()
        it.get()
      }
    }

    val componentsPersistJob = launch {
      val componentModels = components.mapNotNull {
        log.debug {
          "Parsing ${it.remoteAddress}\n${it.responseBody}"
        }

        try {
          gson.fromJson(it.responseBody, OsgiComponentsResponse::class.java)
              ?.data?.firstOrNull()
        } catch (ex: Exception) {
          log.error {
            "Unable to parse:\n${it.remoteAddress}\n${it.responseBody}\nError: $ex"
          }
          null
        }
      }

      componentModels.forEach { componentInfo ->
        log.info { "Persisting component: $componentInfo" }

        aemRepository.addOsgiComponent(
            instance, result, componentInfo
        )
      }
    }
    return componentsPersistJob
  }

  private fun syncServices(servicesInfo: OsgiServicesResponse, instance: AemInstance, session: Session, progress: AemSyncTrackerWrapper, result: SyncPoint): Job {
    val services = runBlocking {
      log.info { "Requesting ${servicesInfo.data.size} services" }
      val servicesResponses: MutableCollection<Response> = ArrayList()
      servicesInfo.data.map { serviceInfo ->
        val serviceUrl = "${instance.address}/system/console/services/${serviceInfo.id}.json"
        log.debug { "Requesting service: ${serviceInfo.id}, url: $serviceUrl" }
        client.prepareGet(serviceUrl)
            .session(session)
            .execute()
      }.mapTo(servicesResponses) {
        progress.progress()
        it.get()
      }

      servicesResponses
    }

    val servicePersistJob = launch {
      val serviceModels = services.mapNotNull {
        log.debug { "Parsing ${it.remoteAddress}\n${it.responseBody}" }

        try {
          gson.fromJson(it.responseBody, OsgiServicesResponse::class.java)
              ?.data?.firstOrNull()
        } catch (ex: Exception) {
          log.error {
            "Unable to parse:\n${it.remoteAddress}\n${it.responseBody}\nError: $ex"
          }
          null
        }
      }

      serviceModels.forEach { serviceModel ->
        log.info {
          "Persisting service: $serviceModel"
        }

        aemRepository.addOsgiService(instance, result, serviceModel)
        progress.progress()
      }
    }
    return servicePersistJob
  }

  private fun trackerWrapper(bundlesInfo: BundlesInfoResponse,
                             servicesInfo: OsgiServicesResponse,
                             componentsInfo: OsgiComponentsResponse,
                             aemSyncProgressTracker: IAemSyncProgressTracker): AemSyncTrackerWrapper {
    val max = (bundlesInfo.data.size + servicesInfo.data.size + componentsInfo.data.size) * 2
    return AemSyncTrackerWrapper(max, tracker = aemSyncProgressTracker)
  }

  override fun bundles(instance: AemInstance, syncPoint: SyncPoint): List<BundleInfo> {
    return aemRepository.allBundles()
  }

  override fun services(instance: AemInstance, syncPoint: SyncPoint): List<OsgiServiceInfo> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun syncPoints(instance: AemInstance): List<SyncPoint> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  companion object {

    /**
     * Creates instance of [AemSyncService].
     */
    fun create(configuration: AemSyncServiceConfiguration,
               log: ILogger = ConsoleLogger()): AemSyncService {
      return AemSyncService(
          InMemoryAemRepository()
      )
    }
  }

}
