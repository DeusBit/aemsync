package org.aemsync.core.http.response

import org.aemsync.core.api.model.OsgiServiceInfo

/**
 * @author Dmytro Primshyts
 */
data class OsgiServicesResponse(
    val status: String,
    val serviceCount: Int,
    val data: List<OsgiServiceInfo>
)
