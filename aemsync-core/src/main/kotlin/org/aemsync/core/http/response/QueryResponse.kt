package org.aemsync.core.http.response

/**
 * @author Dmytro Primshyts
 */
internal data class QueryResponse(
  val results: List<QuerySingleResult>,
  val total: Int,
  val success: Boolean,
  val time: Int
)

internal data class QuerySingleResult(
    val path: String,
    val type: String
)
