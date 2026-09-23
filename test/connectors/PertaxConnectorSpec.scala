/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json

class PertaxConnectorSpec extends ConnectorBaseSpec {

  override protected def localGuiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .localGuiceApplicationBuilder()
      .configure(
        "microservice.services.pertax.port" -> server.port()
      )

  lazy val connector: PertaxConnector = app.injector.instanceOf[PertaxConnector]

  "authorise" must {
    "return the response from pertax-backend" in {
      val responseBody = Json.obj("code" -> "OK", "message" -> "Access granted").toString

      server.stubFor(
        post(urlEqualTo("/pertax/authorise"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withHeader("Content-Type", "application/json")
              .withBody(responseBody)
          )
      )

      val result = connector.authorise().futureValue

      result.status mustBe OK
      result.body mustBe responseBody
    }
  }
}
