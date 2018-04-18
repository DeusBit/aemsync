package org.aemsync.core.api.service.query

import kotlinx.coroutines.experimental.Deferred
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.api.model.query.AemQuery
import org.aemsync.core.api.model.query.AemQueryResult

/**
 * Provides ability to query aem instances using
 * ``
 *
 * @author Dmytro Primshyts
 */
interface IAemQueryService {

  /**
   * Execute given [AemQuery] against given [AemInstance].
   *
   * @param query the query
   * @param instance the aem instance
   *
   * @return deferred query result
   */
  fun execute(query: AemQuery, instance: AemInstance) : Deferred<AemQueryResult>

}

