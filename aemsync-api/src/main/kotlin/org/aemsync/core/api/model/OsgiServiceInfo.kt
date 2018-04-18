package org.aemsync.core.api.model

/**
 * OSGi info provided by Felix Console. Example:
 *```json

{
"id": "1771",
"types": "[java.lang.Runnable]",
"pid": "n/a",
"ranking": "",
"bundleId": 87,
"bundleName": "Oak content repository",
"bundleVersion": "1.1.62",
"bundleSymbolicName": "com.adobe.granite.repository"
}
```
 *
 * @property id service id
 * @property types list of services types
 * @property pid service's pid
 * @property ranking service ranking
 * @property bundleId bundle id
 * @property bundleName bundle name
 * @property bundleVersion bundle version
 * @property bundleSymbolicName bundle symbolic name
 *
 * @author Dmytro Primshyts
 */
data class OsgiServiceInfo(
  val id: String?,
  val types: String?,
  val pid: String?,
  val ranking: String?,
  val bundleId: String?,
  val bundleName: String?,
  val bundleVersion: String?,
  val bundleSymbolicName: String?
)
