package org.aemsync.core.storage

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.aemsync.core.api.model.*
import org.aemsync.core.api.storage.IAemRepository

/**
 * In memory implementation of [IAemRepository].
 * Offers limited functionality: ignores [SyncPoint] objects.
 *
 * @author Dmytro Primshyts
 */
class InMemoryAemRepository : IAemRepository {

  private val instances: MutableList<AemInstance> = ArrayList()
  private val bundleInfos: MutableMap<AemInstance, MutableList<BundleInfo>> = HashMap()
  private val osgiServiceInfos: MutableMap<AemInstance, MutableList<OsgiServiceInfo>> = HashMap()
  private val osgiComponentInfos: MutableMap<AemInstance, MutableList<OsgiComponentInfo>> = HashMap()

  override fun addBundle(instance: AemInstance,
                         syncPoint: SyncPoint,
                         bundleInfo: BundleInfo) {
    val infos = bundleInfos[instance]
    if (infos != null) {
      infos.add(bundleInfo)
    } else {
      bundleInfos[instance] = mutableListOf(bundleInfo)
    }
  }

  override fun allBundles(): List<BundleInfo> {
    throw UnsupportedOperationException("allBundles is deprecated!")
  }

  override fun bundles(instance: AemInstance, syncPoint: SyncPoint): Deferred<List<BundleInfo>> = async {
    bundleInfos[instance] ?: emptyList<BundleInfo>()
  }

  override fun addOsgiService(instance: AemInstance, syncPoint: SyncPoint, serviceModel: OsgiServiceInfo) {
    val infos = osgiServiceInfos[instance]
    if (infos != null) {
      infos.add(serviceModel)
    } else {
      osgiServiceInfos[instance] = mutableListOf(serviceModel)
    }
  }

  override fun addOsgiComponent(instance: AemInstance, syncPoint: SyncPoint, componentModel: OsgiComponentInfo) {
    val infos = osgiComponentInfos[instance]
    if (infos != null) {
      infos += componentModel
    } else {
      osgiComponentInfos[instance] = mutableListOf(componentModel)
    }
  }

  override fun services(instance: AemInstance, syncPoint: SyncPoint): Deferred<List<OsgiServiceInfo>> = async {
    osgiServiceInfos[instance] ?: emptyList<OsgiServiceInfo>()
  }

  override fun components(instance: AemInstance, syncPoint: SyncPoint): Deferred<List<OsgiComponentInfo>> = async {
    osgiComponentInfos[instance] ?: emptyList<OsgiComponentInfo>()
  }

  override fun aemInstances(): List<AemInstance> {
    return listOf(*instances.toTypedArray())
  }

  override fun addAemInstance(instance: AemInstance) {
    instances += instance
  }

  override fun syncPoints(instance: AemInstance): Deferred<List<SyncPoint>> = async {
    emptyList<SyncPoint>()
  }

  override fun close() {
    instances.clear()
    osgiComponentInfos.clear()
    osgiServiceInfos.clear()
    bundleInfos.clear()
  }

  override fun destroy() {
    close()
  }
}
