package org.aemsync.core.api

/**
 * Felix console endpoints.
 *
 * @author Dmytro Primshyts
 */
object FelixConsoleEndpoints {

  const val bundles = "/system/console/bundles.json"
  const val services = "/system/console/services.json"
  const val components = "/system/console/components.json"

  const val configurations = "/system/console/status-Configurations.txt"

  const val productInfo = "/system/console/status-productinfo.txt"

}

/**
 * CRX endpoints.
 *
 * @author Dmytro Primshyts
 */
object CrxEndpoints {

  const val query = "/crx/de/query.jsp"

}
