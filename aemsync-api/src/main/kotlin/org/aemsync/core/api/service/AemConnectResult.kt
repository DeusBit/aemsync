package org.aemsync.core.api.service

/**
 * @author Dmytro Primshyts
 */
enum class AemConnectResult {
  /**
   * Successfully connected.
   */
  OK,

  /**
   * Connection rejected due to wrong credentials.
   */
  WRONG_CREDENTIALS,

  /**
   * No connectivity with supplied address.
   */
  NOT_AVAILABLE
}
