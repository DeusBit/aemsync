package org.aemsync.test

import org.aemsync.core.api.model.AemInstance
import org.mockserver.client.server.MockServerClient
import org.mockserver.integration.ClientAndServer

/**
 * @author Dmytro Primshyts
 */
interface AemIntegrationTest : Aem63It {

  companion object {
    val authorPort = 64502
    val publishPort = 64503
    val authorInstance = AemInstance(
        "Local Author",
        "http://localhost:$authorPort",
        "local"
    )
    val publishInstance = AemInstance(
        "Local Publish",
        "http://localhost:$publishPort",
        "local"
    )
  }

  fun withMockServer(
      fixture: (mockServer: ClientAndServer) -> Unit,
      test: (mockServer: MockServerClient) -> Unit
  ) {
    val mockServerClient = ClientAndServer.startClientAndServer(authorPort, publishPort)

    fixture.invoke(mockServerClient)

    test.invoke(mockServerClient)

    mockServerClient.close()
  }

}
