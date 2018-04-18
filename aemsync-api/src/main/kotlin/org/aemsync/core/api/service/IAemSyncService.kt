package org.aemsync.core.api.service

import kotlinx.coroutines.experimental.Deferred
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.api.model.SyncPoint

/**
 * Aem synchronization service.
 * Contains synchronization operations.
 *
 * @author Dmytro Primshyts
 */
interface IAemSyncService {

  /**
   * Connect sync service to given aem instance.
   *
   * @param instance the instance to connect to
   */
  fun connect(instance: AemInstance): Deferred<AemConnectResult>

  /**
   * Will fetch data from given aem instance.
   *
   * @param instance the aem instance to sync
   * @param aemSyncProgressTracker progress tracker
   * @return job instance
   */
  fun sync(instance: AemInstance,
           aemSyncProgressTracker: IAemSyncProgressTracker = IAemSyncProgressTracker.empty)
      : Deferred<SyncPoint>

  /**
   * Get all available sync points for given aem instance.
   *
   * @param instance aem instance
   * @return list of sync points available for given aem instance
   */
  fun syncPoints(instance: AemInstance): List<SyncPoint>

  /**
   * Get all aem instances.
   *
   * @return list of all aem instances
   */
  fun instances(): List<AemInstance>

  /**
   * Shutdown [IAemSyncService].
   */
  fun shutdown()

  /**
   * Destroy [IAemSyncService]. Will flush underlying data storage.
   */
  fun destroy()

}
