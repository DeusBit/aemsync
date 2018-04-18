package org.aemsync.core.api.service

import org.aemsync.core.api.model.SyncPoint

/**
 * Aem sync progress tracker.
 *
 * @author Dmytro Primshyts
 */
interface IAemSyncProgressTracker {

  /**
   * Notify on current progress.
   *
   * @param progressInfo progress info object
   */
  fun progress(progressInfo: ProgressInfo)

  /**
   * Sync process *done* callback.
   *
   * @param syncPoint new sync point
   */
  fun done(syncPoint: SyncPoint)

  companion object {

    /**
     * Empty progress tracker.
     */
    val empty = object : IAemSyncProgressTracker {
      override fun progress(progressInfo: ProgressInfo) {

      }

      override fun done(syncPoint: SyncPoint) {
      }
    }

  }

  /**
   * Progress info object.
   *
   * @property totalWork total amount of work required for total
   * synchronization (may be inaccurate, `-1` means that the total amount is unknown)
   * @property currentWork amount of work that is done at current moment (`-1` means that the progress is unknown)
   *
   * @property message __optional__ progress message
   */
  data class ProgressInfo(
      val totalWork: Int,
      val currentWork: Int,
      val message: String = ""
  ) {

    /**
     * Adjust current progress to given total.
     *
     * @param newTotal the total to adjust to
     */
    fun adjustTo(newTotal: Int) : Int {
      return currentWork / totalWork * newTotal
    }

  }

}
