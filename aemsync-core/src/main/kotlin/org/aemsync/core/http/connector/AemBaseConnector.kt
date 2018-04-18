package org.aemsync.core.http.connector

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.aemsync.core.api.FelixConsoleEndpoints
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.http.Session
import org.aemsync.core.http.response.*
import org.aemsync.core.util.fromJson
import org.aemsync.core.util.session
import org.asynchttpclient.AsyncHttpClient

/**
 * @author Dmytro Primshyts
 */
class AemBaseConnector(override val client: AsyncHttpClient) : IAemConnector {

  override fun requestVersion(session: Session, instance: AemInstance): Deferred<AemVersionResponse?> = async {
    client.prepareGet("${instance.address}${FelixConsoleEndpoints.productInfo}")
        .session(session)
        .execute().toCompletableFuture()
        .get().run {
          AemVersionResponse(responseBody)
        }
  }

  override fun authenticate(
      instance: AemInstance
  ): Deferred<Session> = async {
    client.preparePost("${instance.address}/libs/granite/core/content/login.html/j_security_check")
        .addFormParam("j_username", instance.credentials.login)
        .addFormParam("j_password", instance.credentials.password)
        .addFormParam("j_validate", "true")
        .execute()
        .toCompletableFuture()
        .get().run {
          Session(cookies)
        }
  }

  override fun requestComponents(
      instance: AemInstance,
      session: Session
  ): Deferred<OsgiComponentsResponse?> = async {
    client
        .prepareGet("${instance.address}${FelixConsoleEndpoints.components}")
        .session(session)
        .execute()
        .toCompletableFuture()
        .get()
        .run {
          fromJson<OsgiComponentsResponse>(responseBody)
        }
  }

  override fun requestConfiguration(
      instance: AemInstance,
      session: Session
  ): Deferred<ConfigurationResponse?> = async {
    client
        .prepareGet("${instance.address}${FelixConsoleEndpoints.configurations}")
        .session(session)
        .execute()
        .toCompletableFuture()
        .get()
        .run {
          ConfigurationResponse(responseBody)
        }
  }

  override fun requestServices(
      instance: AemInstance,
      session: Session
  ): Deferred<OsgiServicesResponse?> = async {
    client
        .prepareGet("${instance.address}${FelixConsoleEndpoints.services}")
        .session(session)
        .execute()
        .toCompletableFuture()
        .get()
        .run {
          fromJson<OsgiServicesResponse>(responseBody)
        }
  }

  override fun requestBundles(
      instance: AemInstance,
      session: Session
  ): Deferred<BundlesInfoResponse?> = async {
    client
        .prepareGet("${instance.address}${FelixConsoleEndpoints.bundles}")
        .session(session)
        .execute()
        .toCompletableFuture()
        .get()
        .run {
          fromJson<BundlesInfoResponse>(responseBody)
        }
  }

}
