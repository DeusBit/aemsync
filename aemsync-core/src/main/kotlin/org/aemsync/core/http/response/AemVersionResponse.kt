package org.aemsync.core.http.response

import org.aemsync.core.api.model.AemVersion

/**
 * @author Dmytro Primshyts
 */
class AemVersionResponse(
    private val responseBody: String
) {
  fun parse(): AemVersion {
    val rawVersion = responseBody.substringAfterLast("(")
        .substringBefore(")")

    return AemVersion.fromRaw(rawVersion)
  }
}
