package org.aemsync.core.api.storage

import kotlinx.coroutines.experimental.Deferred
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.api.model.BundleInfo
import org.aemsync.core.api.model.OsgiComponentInfo
import org.aemsync.core.api.model.OsgiServiceInfo
import org.aemsync.core.api.model.SyncPoint

/**
 * @author Dmytro Primshyts
 */
interface IAemRepository {

  /**
   * Close the repository.
   */
  fun close()

  /**
   * Destroy the repository.
   */
  fun destroy()

  /**
   * Add bundle.
   *
   * @param instance the aem instance
   * @param syncPoint the sync point
   * @param bundleInfo the bundle info
   */
  fun addBundle(instance: AemInstance, syncPoint: SyncPoint, bundleInfo: BundleInfo)

  /**
   * Get all [BundleInfo] objects stored in the repository.
   */
  @Deprecated("To be removed")
  fun allBundles(): List<BundleInfo>

  /**
   * Get all [BundleInfo] objects for given [AemInstance] and [SyncPoint].
   *
   * @param instance the aem instance
   * @param syncPoint the sync point ([SyncPoint.latest] by __default__)
   * @return deferred list of bundle info objects
   */
  fun bundles(instance: AemInstance, syncPoint: SyncPoint = SyncPoint.latest(instance)) : Deferred<List<BundleInfo>>

  fun addOsgiService(instance: AemInstance, syncPoint: SyncPoint, serviceModel: OsgiServiceInfo)

  fun addOsgiComponent(instance: AemInstance, syncPoint: SyncPoint, componentModel: OsgiComponentInfo)

  /**
   * Get all osgi service of given aem instance and sync point.
   *
   * @param instance the aem instance
   * @param syncPoint the sync point (the latest by default)
   * @return deferred list of osgi services
   */
  fun services(instance: AemInstance, syncPoint: SyncPoint = SyncPoint.latest(instance)): Deferred<List<OsgiServiceInfo>>

  /**
   * Get all osgi components of given aem instance and sync point.
   *
   * @param instance the aem instance
   * @param syncPoint the sync point (the latest by default)
   * @return deferred list of osgi components
   */
  fun components(instance: AemInstance, syncPoint: SyncPoint = SyncPoint.latest(instance)): Deferred<List<OsgiComponentInfo>>

  /**
   * Get all aem instances.
   *
   * @return deferred list of all aem instances
   */
  fun aemInstances() : List<AemInstance>

  /**
   * Add given aem instance to storage.
   *
   * @param instance aem instance to persist
   */
  fun addAemInstance(instance: AemInstance)

  /**
   * Get all sync points for given aem instance.
   *
   * @param instance the aem instance
   * @return deferred list of sync points
   */
  fun syncPoints(instance: AemInstance): Deferred<List<SyncPoint>>

}
