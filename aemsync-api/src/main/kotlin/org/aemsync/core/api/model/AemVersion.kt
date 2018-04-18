package org.aemsync.core.api.model

/**
 * Aem version.
 *
 * @property version the version
 * @property detailedVersion raw (unparsed) version
 *
 * @author Dmytro Primshyts
 */
data class AemVersion(
    val version: MajorVersions,
    val detailedVersion: String
) {

  /**
   * Enumeration that represents main major AEM
   * versions.
   */
  enum class MajorVersions {
    AEM6_0, AEM6_1, AEM6_2, AEM6_3, AEM6_4,
    UNKNOWN
  }

  companion object {

    /**
     * Convert raw string into [AemVersion] object.
     *
     * @param versionString raw version string
     * @return aem version
     */
    fun fromRaw(versionString: String): AemVersion {
      with(versionString) {
        return when {
          startsWith("6.0") -> AemVersion(MajorVersions.AEM6_0, versionString)
          startsWith("6.1") -> AemVersion(MajorVersions.AEM6_1, versionString)
          startsWith("6.2") -> AemVersion(MajorVersions.AEM6_2, versionString)
          startsWith("6.3") -> AemVersion(MajorVersions.AEM6_3, versionString)
          startsWith("6.4") -> AemVersion(MajorVersions.AEM6_4, versionString)
          else -> AemVersion(MajorVersions.UNKNOWN, versionString)
        }
      }
    }

    val unknown: AemVersion
      get() = fromRaw("unknown")
  }
}
