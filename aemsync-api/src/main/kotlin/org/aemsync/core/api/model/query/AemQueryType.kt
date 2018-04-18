package org.aemsync.core.api.model.query

/**
 * @author Dmytro Primshyts
 */
enum class AemQueryType(
    val representation: String
) {
  SQL("SQL"),
  SQL2("JCR-SQL2"),
  XPATH("XPATH")
}
