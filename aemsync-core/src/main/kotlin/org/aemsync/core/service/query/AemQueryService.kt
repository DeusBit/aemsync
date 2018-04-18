package org.aemsync.core.service.query

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.aemsync.core.api.CrxEndpoints
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.api.model.query.AemQuery
import org.aemsync.core.api.model.query.AemQueryResult
import org.aemsync.core.api.model.query.AemQuerySingle
import org.aemsync.core.api.service.query.IAemQueryService
import org.aemsync.core.http.connector.IAemSessionAwareConnector
import org.aemsync.core.http.response.QueryResponse
import org.aemsync.core.util.fromJson
import org.aemsync.core.util.session

/**
 * @author Dmytro Primshyts
 */
class AemQueryService(
    val connector: IAemSessionAwareConnector
) : IAemQueryService {
  override fun execute(query: AemQuery, instance: AemInstance): Deferred<AemQueryResult> = async {
    val response = connector.client
        .prepareGet("${instance.address}${CrxEndpoints.query}")
        .addQueryParam("stmt", query.query)
        .addQueryParam("_charset_", "UTF-8")
        .addQueryParam("type", query.type.representation)
        .addQueryParam("showResults", "true")
        .session(connector.session(instance).await())
        .execute()
        .toCompletableFuture()
        .get()
        .run {
          fromJson<QueryResponse>(responseBody)
        }

    response?.toAemQueryResult()
        ?: AemQueryResult.Fail()
  }

  private fun QueryResponse.toAemQueryResult(): AemQueryResult {
    return AemQueryResult.Ok(
        results = results.map {
          AemQuerySingle(
              it.path,
              it.type
          )
        },
        total = total,
        time = time
    )
  }
}

