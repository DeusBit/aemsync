package org.aemsync.core.http.connector

import kotlinx.coroutines.experimental.Deferred
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.http.IWithAsyncClient
import org.aemsync.core.http.Session
import org.aemsync.core.http.response.AemVersionResponse
import org.aemsync.core.http.response.BundlesInfoResponse
import org.aemsync.core.http.response.ConfigurationResponse
import org.aemsync.core.http.response.OsgiComponentsResponse
import org.aemsync.core.http.response.OsgiServicesResponse

/**
 * @author Dmytro Primshyts
 */
interface IAemConnector : IWithAsyncClient {

  /**
   * Request aem version.
   *
   * @param session the session
   * @param instance instance to check version
   *
   * @return deferred aem version object
   */
  fun requestVersion(
      session: Session,
      instance: AemInstance
  ): Deferred<AemVersionResponse?>

  /**
   * Authenticate to given aem instance.
   *
   * @param instance instance to authenticate
   * @return deferred session object
   */
  fun authenticate(
      instance: AemInstance
  ): Deferred<Session>

  /**
   * Request information on all OSGi components.
   *
   * @param instance instance to fetch data from
   * @param session session to use
   * @return deferred components response
   */
  fun requestComponents(
      instance: AemInstance,
      session: Session
  ): Deferred<OsgiComponentsResponse?>

  /**
   * Request configuration.
   *
   * @param instance instance to fetch data from
   * @param session session to use
   * @see [FelixConsoleEndpoints.configurations]
   * @return deferred configuration response
   */
  fun requestConfiguration(
      instance: AemInstance,
      session: Session
  ): Deferred<ConfigurationResponse?>

  /**
   * Request information on all OSGi services.
   *
   * @param instance instance to fetch data from
   * @param session session to use
   * @return deferred services response
   */
  fun requestServices(
      instance: AemInstance,
      session: Session
  ): Deferred<OsgiServicesResponse?>

  /**
   * Request information on all OSGi bundles.
   *
   * @param instance instance to fetch data from
   * @param session session to use
   * @return deferred bundles response
   */
  fun requestBundles(
      instance: AemInstance,
      session: Session
  ): Deferred<BundlesInfoResponse?>


}
