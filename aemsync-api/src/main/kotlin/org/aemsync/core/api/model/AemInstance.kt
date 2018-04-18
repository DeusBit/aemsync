package org.aemsync.core.api.model

/**
 * AEM Instance connection descriptor model.
 *
 * @property name name of the instance (e.g. "Dev Author")
 * @property address address of the instance (e.g. "localhost:4502")
 * @property credentials credentials
 *
 * @author Dmytro Primshyts
 */
data class AemInstance(
    val name: String,
    val address: String,
    val group: String = "",
    val credentials: AemCredentials = AemCredentials.default,
    val version: AemVersion = AemVersion.unknown
) {

  override fun equals(other: Any?): Boolean {
    val otherInstance = other as? AemInstance ?: return false

    return address == otherInstance.address
        && credentials == otherInstance.credentials
  }

  override fun hashCode(): Int {
    return address.hashCode() or credentials.hashCode() or version.hashCode()
  }
}

/**
 * Data holder for AEM credentials.
 *
 * @property login the login
 * @property password the password
 */
data class AemCredentials(
  val login: String,
  val password: String
) {
  override fun toString(): String = "AemCredentials[name=$login,password=******]"

  companion object {
    /**
     * Default AEM credentials - admin:admin
     */
    val default = AemCredentials("admin", "admin")
  }

}
