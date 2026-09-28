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
import play.api.test.FakeRequest
import uk.gov.hmrc.play.partials.HtmlPartial

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

  "loadPartial" must {
    "return the partial from pertax-backend" in {
      server.stubFor(
        get(urlEqualTo("/path/for/partial"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withHeader("Content-Type", "text/html")
              .withHeader("X-Title", "Partial%20title")
              .withBody("<p>Partial content</p>")
          )
      )

      val result = connector.loadPartial("/path/for/partial")(FakeRequest(), ec).futureValue

      result mustBe HtmlPartial.Success(Some("Partial title"), play.twirl.api.Html("<p>Partial content</p>"))
    }

    "return a failure partial when pertax-backend responds with an error" in {
      server.stubFor(
        get(urlEqualTo("/path/for/partial"))
          .willReturn(
            aResponse()
              .withStatus(500)
              .withBody("Partial unavailable")
          )
      )

      val result = connector.loadPartial("/path/for/partial")(FakeRequest(), ec).futureValue

      result mustBe a[HtmlPartial.Failure]
    }
  }
}
