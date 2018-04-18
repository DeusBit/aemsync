package org.aemsync.core.service.aemsync.impl

import kotlinx.coroutines.experimental.runBlocking
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.api.model.BundleInfo
import org.aemsync.core.api.model.OsgiComponentInfo
import org.aemsync.core.api.model.OsgiServiceInfo
import org.aemsync.core.api.model.SyncPoint
import org.aemsync.core.api.service.IAemSimpleOperations
import org.aemsync.core.api.storage.IAemRepository
import org.aemsync.core.api.storage.UnknownAemInstance

/**
 * @author Dmytro Primshyts
 */
class AemSimpleOperations(
    private val aemRepository: IAemRepository
) : IAemSimpleOperations {

  override fun instance(name: String, group: String): AemInstance? {
    return aemRepository.aemInstances().find {
      it.name == name && (group == "" || it.group == group)
    }
  }

  override fun instance(instance: AemInstance): AemInstance = runBlocking {
    aemRepository.aemInstances().find { it == instance }
        ?: throw UnknownAemInstance(instance)
  }

  override fun bundles(instance: AemInstance, syncPoint: SyncPoint): List<BundleInfo> = runBlocking {
    val additionalInfo = instance(instance)
    aemRepository.bundles(additionalInfo, syncPoint).await()
  }

  override fun services(instance: AemInstance, syncPoint: SyncPoint): List<OsgiServiceInfo> = runBlocking {
    val additionalInfo = instance(instance)
    aemRepository.services(additionalInfo, syncPoint).await()
  }

  override fun components(instance: AemInstance, syncPoint: SyncPoint): List<OsgiComponentInfo> = runBlocking {
    val additionalInfo = instance(instance)
    aemRepository.components(additionalInfo, syncPoint).await()
  }
}
