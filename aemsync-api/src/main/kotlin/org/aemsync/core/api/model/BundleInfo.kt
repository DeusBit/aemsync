package org.aemsync.core.api.model

/**
 * @author Dmytro Primshyts
 */
data class BundleInfo(
    val id: Int,
    val name: String?,
    val fragment: Boolean,
    val stateRaw: Int,
    val state: String?,
    val version: String?,
    val symbolicName: String?,
    val category: String?
)
