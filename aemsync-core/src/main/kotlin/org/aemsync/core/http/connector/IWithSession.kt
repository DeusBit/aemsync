package org.aemsync.core.http.connector

import kotlinx.coroutines.experimental.Deferred
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.http.Session

/**
 * @author Dmytro Primshyts
 */
interface IWithSession {

  /**
   * Get [Session] for given [AemInstance].
   *
   * @param instance the aem instance
   * @return deferred session object
   */
  fun session(instance: AemInstance): Deferred<Session>

}
