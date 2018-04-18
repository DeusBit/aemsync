package org.aemsync.core.service.aemsync.progress

import org.aemsync.core.api.model.SyncPoint
import org.aemsync.core.api.service.IAemSyncProgressTracker

/**
 * @author Dmytro Primshyts
 */
class AemSyncTrackerWrapper(
  max: Int = -1,
  currentWork: Int = -1,
  private val tracker: IAemSyncProgressTracker
) {
  private var progressInfo: IAemSyncProgressTracker.ProgressInfo =
    IAemSyncProgressTracker.ProgressInfo(max, currentWork)

  /**
   * Progress underlying [IAemSyncProgressTracker] by given amount.
   *
   * @param amount the amount of work done
   * @param totalDiff the total work diff against current total work
   * @param message an optional progress message
   */
  fun progress(
    amount: Int = 1,
    totalDiff: DiffInt = DiffInt.Nop(),
    message: String = ""
  ) {
    progressInfo = progressInfo.copy(
      currentWork = progressInfo.currentWork + amount,
      totalWork = totalDiff(progressInfo.totalWork),
      message = message
    )

    tracker.progress(progressInfo)
  }

  /**
   * Send message to underlying [IAemSyncProgressTracker].
   * [IAemSyncProgressTracker.ProgressInfo.totalWork] &
   * [IAemSyncProgressTracker.ProgressInfo.currentWork] won't be affected.
   *
   * @param message the message to send
   */
  fun message(message: String) {
    progressInfo = progressInfo.copy(
      message = message
    )

    tracker.progress(progressInfo)
  }

  /**
   * Send "done" notification to underlying tracker.
   *
   * @param syncPoint the sync point
   */
  fun done(syncPoint: SyncPoint) = tracker.done(syncPoint)

}

sealed class DiffInt(
  val value: Int
) {

  abstract operator fun invoke(input: Int): Int

  class Add(amount: Int) : DiffInt(amount) {
    override fun invoke(input: Int): Int = input + value
  }

  class Substract(amount: Int) : DiffInt(amount) {
    override fun invoke(input: Int): Int = input - value
  }

  /**
   * Overrides the input with given value.
   */
  class Absolute(amount: Int) : DiffInt(amount) {
    override fun invoke(input: Int): Int = value
  }

  /**
   * Empty diff
   */
  class Nop : DiffInt(0) {
    override fun invoke(input: Int): Int = input
  }

}
