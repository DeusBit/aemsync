package org.aemsync.core.api.model.query

import org.intellij.lang.annotations.Language

/**
 * @author Dmytro Primshyts
 */
sealed class AemQuery constructor(
    val query: String,
    val type: AemQueryType = AemQueryType.SQL2
) {

  /**
   * [AemQueryType.SQL2] query data holder.
   */
  class Sql2Query(query: String) : AemQuery(
      query, AemQueryType.SQL2
  )

  /**
   * [AemQueryType.XPATH] query data holder.
   */
  class XpathQuery(@Language("XPath") xpath: String) : AemQuery(
      xpath, AemQueryType.XPATH
  )

}

