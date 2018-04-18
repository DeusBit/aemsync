package org.aemsync.core.service.query

import kotlinx.coroutines.experimental.runBlocking
import org.aemsync.core.api.model.query.AemQuery
import org.aemsync.core.api.model.query.AemQueryResult
import org.aemsync.core.api.model.query.AemQuerySingle
import org.aemsync.core.http.connector.AemBaseConnector
import org.aemsync.core.http.connector.AemSessionAwareConnector
import org.aemsync.test.AemIntegrationTest
import org.aemsync.test.AemIntegrationTest.Companion.authorInstance
import org.assertj.core.api.Assertions.assertThat
import org.asynchttpclient.Dsl.asyncHttpClient
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

/**
 * Test for [AemQueryService].
 *
 * @author Dmytro Primshyts
 */
class AemQueryServiceTest : AemIntegrationTest {

  @Test
  fun `test positive SQL2 query`() = withMockServer({ cas: ClientAndServer ->
    cas.apply {
      positiveLogin()
      `when`(
          request()
              .withMethod("GET")
              .withQueryStringParameter("type", "JCR-SQL2")
              .withQueryStringParameter("stmt", "select * from [nt:base]")
              .withQueryStringParameter("_charset_", "UTF-8")
              .withQueryStringParameter("showResults", "true")
      )
          .respond(
              response()
                  .withBody(readFile("./fixture/aem61/query/positive-sql2.json"))
          )
    }
  }, {
    runBlocking {

      val tested = AemQueryService(AemSessionAwareConnector(AemBaseConnector(asyncHttpClient())))

      val result = tested.execute(AemQuery.Sql2Query("select * from [nt:base]"), authorInstance)
          .await()

      assertThat(result)
          .isInstanceOf(AemQueryResult.Ok::class.java)

      val ok = result as AemQueryResult.Ok

      assertThat(ok.results)
          .containsExactlyInAnyOrder(
              AemQuerySingle("/content/example/en/page1/jcr:content", "cq:PageContent"),
              AemQuerySingle("/content/example/en/page2/jcr:content", "cq:PageContent")
          )

      assertThat(ok.total)
          .isEqualTo(-1)

      assertThat(ok.time)
          .isEqualTo(1)
    }
  })

}
