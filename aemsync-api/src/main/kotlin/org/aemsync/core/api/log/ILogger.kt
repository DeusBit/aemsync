package org.aemsync.core.api.log

/**
 * @author Dmytro Primshyts
 */
interface ILogger {

  /**
   * Log "info" level message.
   *
   * @param message lazy message provider
   */
  fun info(message: () -> String)

  /**
   * Log "error" level message.
   *
   * @param message lazy message provider
   */
  fun error(message: () -> String)

  /**
   * Log "debug" level message.
   *
   * @param message lazy message provider
   */
  fun debug(message: () -> String)

}

class ConsoleLogger : ILogger {
  override fun info(message: () -> String) {
    println("Info: ${message()}")
  }

  override fun error(message: () -> String) {
    println("Error: ${message()}")
  }

  override fun debug(message: () -> String) {
    println("Debug: ${message()}")
  }

}
