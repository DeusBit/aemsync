package org.aemsync.core.http.connector

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.http.Session
import org.aemsync.core.http.response.*
import org.asynchttpclient.AsyncHttpClient

/**
 * @author Dmytro Primshyts
 */
class AemSessionAwareConnector(
    private val aemConnector: IAemConnector
) : IAemSessionAwareConnector {
  override fun requestVersion(instance: AemInstance): Deferred<AemVersionResponse?> = async {
    aemConnector.requestVersion(session(instance).await(), instance).await()
  }

  override fun authenticate(instance: AemInstance): Deferred<Session> = aemConnector.authenticate(instance)

  override fun requestComponents(instance: AemInstance): Deferred<OsgiComponentsResponse?> = async {
    aemConnector.requestComponents(instance, session(instance).await()).await()
  }

  override fun requestConfiguration(instance: AemInstance): Deferred<ConfigurationResponse?> = async {
    aemConnector.requestConfiguration(instance, session(instance).await()).await()
  }

  override fun requestServices(instance: AemInstance): Deferred<OsgiServicesResponse?> = async {
    aemConnector.requestServices(instance, session(instance).await()).await()
  }

  override fun requestBundles(instance: AemInstance): Deferred<BundlesInfoResponse?> = async {
    aemConnector.requestBundles(instance, session(instance).await()).await()
  }

  override val client: AsyncHttpClient
    get() = aemConnector.client

  private val map: MutableMap<AemInstance, Session> = HashMap()
  override fun session(instance: AemInstance): Deferred<Session> = async {
    map.computeIfAbsent(instance) {
      runBlocking {
        authenticate(instance).await()
      }
    }
  }

}
