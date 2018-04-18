package org.aemsync.core.http.response

import org.aemsync.core.api.model.BundleInfo

/**
 * @author Dmytro Primshyts
 */
data class BundlesInfoResponse(
    val status: String,
    val s: List<Int>,
    val data: List<BundleInfo>
)
