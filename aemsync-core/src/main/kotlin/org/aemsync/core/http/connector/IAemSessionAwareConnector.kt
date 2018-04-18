package org.aemsync.core.http.connector

import kotlinx.coroutines.experimental.Deferred
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.http.IWithAsyncClient
import org.aemsync.core.http.Session
import org.aemsync.core.http.response.*

/**
 * Session aware version of [IAemConnector], intended to be used as convenience **wrapper**
 * around original [IAemConnector].
 *
 * @author Dmytro Primshyts
 */
interface IAemSessionAwareConnector : IWithAsyncClient, IWithSession {

  /**
   * Request aem version.
   *
   * @param instance instance to check version
   *
   * @return deferred aem version object
   */
  fun requestVersion(instance: AemInstance): Deferred<AemVersionResponse?>

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
   * @return deferred components response
   */
  fun requestComponents(
      instance: AemInstance
  ): Deferred<OsgiComponentsResponse?>

  /**
   * Request configuration.
   *
   * @param instance instance to fetch data from
   * @see [FelixConsoleEndpoints.configuration]
   * @return deferred configuration response
   */
  fun requestConfiguration(
      instance: AemInstance
  ): Deferred<ConfigurationResponse?>

  /**
   * Request information on all OSGi services.
   *
   * @param instance instance to fetch data from
   * @return deferred services response
   */
  fun requestServices(
      instance: AemInstance
  ): Deferred<OsgiServicesResponse?>

  /**
   * Request information on all OSGi bundles.
   *
   * @param instance instance to fetch data from
   * @return deferred bundles response
   */
  fun requestBundles(
      instance: AemInstance
  ): Deferred<BundlesInfoResponse?>

}
