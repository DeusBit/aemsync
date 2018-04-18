package org.aemsync.core.http.response

import org.aemsync.core.api.model.OsgiComponentInfo

/**
 * @author Dmytro Primshyts
 */
data class OsgiComponentsResponse(
    val status: String,
    val data: List<OsgiComponentInfo>
)
