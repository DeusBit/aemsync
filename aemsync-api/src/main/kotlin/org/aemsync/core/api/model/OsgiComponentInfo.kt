package org.aemsync.core.api.model

/**
 * @author Dmytro Primshyts
 */
data class OsgiComponentInfo(
  val id: String?,
  val name: String?,
  val pid: String?,
  val state: String?,
  val stateRaw: String?,
  val bundleId: String?,
  val configurable: String?,
  val props: Map<String, String>
)
