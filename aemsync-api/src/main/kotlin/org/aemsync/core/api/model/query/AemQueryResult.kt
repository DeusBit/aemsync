package org.aemsync.core.api.model.query

/**
 * @author Dmytro Primshyts
 */
sealed class AemQueryResult {

  class Ok(
      val results: List<AemQuerySingle>,
      val total: Int,
      val time: Int
  ) : AemQueryResult()

  class Fail : AemQueryResult()

}

data class AemQuerySingle(
    val path: String,
    val type: String
)
