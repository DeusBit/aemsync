package org.aemsync.core.http.response

/**
 * @author Dmytro Primshyts
 */
class ConfigurationResponse(
  val responseBody: String
) {

  /**
   * Convert configuration response into list of [ConfigurationEntry] objects.
   *
   * @return list of configuration entries
   */
  fun parse(): List<ConfigurationEntry> {
    val lines = responseBody.split("\n")

    val result: MutableList<ConfigurationEntry> = ArrayList()

    var currentItem: ConfigurationEntry? = null

    for (line in lines) {
      if (currentItem == null && line.startsWith("PID")) {
        val splitted = line.split(" = ")
        val mainPid = if (splitted.size == 2) {
          splitted[1]
        } else {
          continue
        }
        currentItem = ConfigurationEntry(pid = mainPid)
        continue
      }

      if (line.trim().isEmpty() && currentItem != null) {
        result += currentItem
        currentItem = null
        continue
      }

      if (currentItem != null) {
        val keyVal = line.split(" = ")
        currentItem.consumeProperty(keyVal)
      }
    }
    return result
  }

  private fun ConfigurationEntry.consumeProperty(rawProperty: List<String>) {
    val key = rawProperty[0].trim()
    val value = if (rawProperty.size > 1) {
      rawProperty[1]
    } else {
      ""
    }

    when {
      key == "service.pid" -> servicePid = value
      key == "Factory PID" -> factoryPid = value
      key == "service.factoryPid" -> serviceFactoryPid = value
      key == "Bundle Location" -> bundleLocation = value
      else -> properties += key to value
    }
  }
}

/**
 * Represents single configuration entry.
 *
 * @property pid PID - represents component pid
 * @property servicePid `service.pid` - the service pid (empty for non-service)
 *
 * @property factoryPid `Factory PID` - factory pid (empty for non-factory component)
 * @property serviceFactoryPid `service.factoryPid` - service factory pid
 *
 * @property bundleLocation "BundleLocation" property
 * @property properties list of configuration properties
 */
internal class ConfigurationEntry(
  var pid: String = "",
  var servicePid: String = "",
  var factoryPid: String = "",
  var serviceFactoryPid: String = "",
  var bundleLocation: String = "",
  val properties: MutableMap<String, String> = HashMap()
)
