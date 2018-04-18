package org.aemsync.core.service.aemsync.impl

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.aemsync.core.api.log.ConsoleLogger
import org.aemsync.core.api.log.ILogger
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.api.model.SyncPoint
import org.aemsync.core.api.service.*
import org.aemsync.core.api.storage.IAemRepository
import org.aemsync.core.http.Session
import org.aemsync.core.http.connector.AemBaseConnector
import org.aemsync.core.http.connector.AemSessionAwareConnector
import org.aemsync.core.http.connector.IAemSessionAwareConnector
import org.aemsync.core.service.aemsync.progress.AemSyncTrackerWrapper
import org.aemsync.core.service.aemsync.progress.DiffInt
import org.aemsync.core.storage.InMemoryAemRepository
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl.asyncHttpClient
import org.asynchttpclient.Dsl.config
import java.util.*

/**
 * @author Dmytro Primshyts
 */
class AemLazySyncService private constructor(
    private val aemRepository: IAemRepository,
    private val log: ILogger = ConsoleLogger(),
    override val client: AsyncHttpClient = asyncHttpClient(
        config()
            .setRequestTimeout(20_000)
    ),
    override val simpleOperations: IAemSimpleOperations = AemSimpleOperations(aemRepository),
    aemConnector: IAemSessionAwareConnector
) :
    IAemSessionAwareConnector by aemConnector,
    IAemSimpleOperations by simpleOperations,
    IAemSyncService,
    IAemIntegrationFacade {

  override val syncService
    get() = this

  override fun connect(instance: AemInstance): Deferred<AemConnectResult> = async {
    runBlocking {
      val versionResponse = requestVersion(instance).await()
          ?: return@runBlocking AemConnectResult.NOT_AVAILABLE

      val parsed = versionResponse.parse()

      val newInstance = instance.copy(
          version = parsed
      )

      aemRepository.addAemInstance(newInstance)

      AemConnectResult.OK
    }
  }

  override fun instances(): List<AemInstance> {
    return aemRepository.aemInstances()
  }

  override fun sync(
      instance: AemInstance,
      aemSyncProgressTracker: IAemSyncProgressTracker
  ): Deferred<SyncPoint> = async {
    val startTime = System.currentTimeMillis()
    instance(instance)

    val syncPoint = SyncPoint(
        Date().toString(),
        instance
    )

    val progress = AemSyncTrackerWrapper(tracker = aemSyncProgressTracker)
    progress.message("Initializing...")

    val session: Session = authenticate(instance).await()

    val bundlesResult = launch {
      requestBundles(instance)
          .await()
          ?.let {
            log.info {
              "Saving bundles response:\n$it"
            }

            progress.progress(
                totalDiff = DiffInt.Add(it.data.size),
                message = "Saving ${it.data.size} bundles..."
            )

            it.data.forEach { bundleInfo ->
              aemRepository.addBundle(
                  instance,
                  syncPoint,
                  bundleInfo
              )
            }
          }
    }

    val componentsResult = launch {
      val configuration = requestConfiguration(instance).await()
          ?: throw RuntimeException("Unable to retrieve OSGi configuration for $instance")

      val components = requestComponents(instance).await()
          ?: throw RuntimeException("Unable to retrieve OSGi configuration for $instance")

      val configs = configuration.parse()

      components.data.forEach { component ->
        val config = configs.find {
          it.pid == component.name
        }

        val componentToSave = if (config != null) {
          component.copy(
              props = config.properties
          )
        } else {
          component
        }

        aemRepository.addOsgiComponent(
            instance,
            syncPoint,
            componentToSave
        )
      }
    }

    val servicesResult = launch {
      requestServices(instance)
          .await()
          ?.let {
            log.info {
              "Saving services response:\n$it"
            }

            it.data.forEach { serviceInfo ->
              aemRepository.addOsgiService(
                  instance,
                  syncPoint,
                  serviceInfo
              )
            }
          }
    }

    bundlesResult.join()
    componentsResult.join()
    servicesResult.join()

    val endTime = System.currentTimeMillis()

    log.info { "Synchronization completed in ${endTime - startTime}ms" }

    syncPoint
  }

  override fun syncPoints(instance: AemInstance): List<SyncPoint> = runBlocking {
    val additionalInfo = instance(instance)
    aemRepository.syncPoints(additionalInfo).await()
  }

  override fun shutdown() {
    log.info { "Shutting down..." }
    aemRepository.close()
    client.close()
  }

  override fun destroy() {
    log.info { "Destroying..." }
    aemRepository.destroy()
  }

  companion object {

    /**
     * Builder method for [AemLazySyncService].
     *
     * @param repository the repository
     * @param log logger
     * @param client async http client
     * @param aemConnector aem connector
     *
     * @return instance of aem lazy sync service
     */
    fun create(
        repository: IAemRepository = InMemoryAemRepository(),
        log: ILogger = ConsoleLogger(),
        client: AsyncHttpClient = asyncHttpClient(config().setRequestTimeout(20_000)),
        aemConnector: IAemSessionAwareConnector = AemSessionAwareConnector(AemBaseConnector(client))
    ): AemLazySyncService {
      return AemLazySyncService(
          repository,
          log,
          client,
          aemConnector = aemConnector
      )
    }
  }

}
