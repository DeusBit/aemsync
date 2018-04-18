package org.aemsync.test

import org.aemsync.core.api.FelixConsoleEndpoints
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.Parameter
import org.mockserver.model.ParameterBody

/**
 * @author Dmytro Primshyts
 */
interface Aem63It {

  fun ClientAndServer.positiveLogin() =
      `when`(
          request()
              .withMethod("POST")
              .withPath("/libs/granite/core/content/login.html/j_security_check")
              .withBody(
                  ParameterBody.params(
                      Parameter.param("j_username", "admin"),
                      Parameter.param("j_password", "admin"),
                      Parameter.param("j_validate", "true")
                  )
              )
      )
          .respond(
              response()
                  .withCookie(
                      "login-token", "token_value"
                  )
          )

  fun ClientAndServer.mockBundles() =
      `when`(
          request()
              .withMethod("GET")
              .withPath(FelixConsoleEndpoints.bundles)
      )
          .respond(
              response()
                  .withBody(readFile("./fixture/aem63/bundles.json"))
          )

  fun ClientAndServer.mockServices() =
      `when`(
          request()
              .withMethod("GET")
              .withPath(FelixConsoleEndpoints.services)
      )
          .respond(
              response()
                  .withBody(readFile("./fixture/aem63/services.json"))
          )

  fun ClientAndServer.mockComponents() =
      `when`(
          request()
              .withMethod("GET")
              .withPath(FelixConsoleEndpoints.components)
      )
          .respond(
              response()
                  .withBody(readFile("./fixture/aem63/components.json"))
          )

  fun ClientAndServer.mockConfiguration() =
    `when`(
      request()
        .withMethod("GET")
        .withPath(FelixConsoleEndpoints.configurations)
    )
      .respond(
        response()
          .withBody(readFile("./fixture/aem63/configuration.txt"))
      )

  fun ClientAndServer.mockVersion() =
      `when`(
          request()
              .withMethod("GET")
              .withPath("/system/console/status-productinfo.txt")
      )
          .respond(
              response()
                  .withBody(readFile("./fixture/aem63/productInfo.txt"))
          )

  fun readFile(file: String): String {
    val input = Aem63It::class.java.classLoader
        .getResourceAsStream(file)

    return String(input.use { it.readBytes() })
  }

}
