package org.aemsync.core.service

/**
 * Aem sync service configuration entity.
 *
 * @property storagePath the storage path
 *
 * @author Dmytro Primshyts
 */
data class AemSyncServiceConfiguration(
    val storagePath: String
) {

  companion object {

    /**
     * Create configuration within current user's home directory.
     *
     * @param path the path under user's home
     */
    fun inUserHome(path: String = ""): AemSyncServiceConfiguration =
        AemSyncServiceConfiguration(System.getProperty("user.home") + "/$path")

  }

}
