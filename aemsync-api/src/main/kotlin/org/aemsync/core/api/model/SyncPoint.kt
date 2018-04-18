package org.aemsync.core.api.model

/**
 * @author Dmytro Primshyts
 */
data class SyncPoint(
    val date: String,
    val aemInstance: AemInstance
) {

  companion object {
    fun latest(aemInstance: AemInstance) = SyncPoint("latest", aemInstance)
  }

}
