/*
 * Copyright 2024 HM Revenue & Customs
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
import fixtures.WireMockHelper
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.domain.{Generator, Nino}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Random
class FandFConnectorSpec extends ConnectorBaseSpec with WireMockHelper {

  override protected def localGuiceApplicationBuilder(): GuiceApplicationBuilder = GuiceApplicationBuilder()
    .configure(
      "microservice.services.fandf.port" -> server.port(),
      "microservice.services.fandf.host"                  -> "127.0.0.1"
    )

  override implicit lazy val app: Application = localGuiceApplicationBuilder().build()

  lazy val fandfConnector: FandFConnector = app.injector.instanceOf[FandFConnector]
  private def randomNino(): Nino          = new Generator(new Random()).nextNino
  def url                                 = s"/delegation/get"

  "FandFConnector" must {
    "return None when NOT FOUND" in {
      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(notFound())
      )

      val result = Await.result(fandfConnector.getTrustedHelper(), Duration.Inf)
      result mustBe None
    }
    "return None when OK and empty json" in {
      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(ok("{}"))
      )

      val result = Await.result(fandfConnector.getTrustedHelper(), Duration.Inf)
      result mustBe None
    }
    "return helper when OK and full json" in {
      val trustedHelperNino: Nino            = randomNino()
      val fandfTrustedHelperResponse: String =
        s"""
           |{
           |   "principalName": "principal Name",
           |   "attorneyName": "attorneyName",
           |   "returnLinkUrl": "returnLink",
           |   "principalNino": "$trustedHelperNino"
           |}
           |""".stripMargin
      val trustedHelper: TrustedHelper       =
        TrustedHelper("principal Name", "attorneyName", "returnLink", Some(trustedHelperNino.nino))
      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(ok(fandfTrustedHelperResponse))
      )

      val result = Await.result(fandfConnector.getTrustedHelper(), Duration.Inf)
      result mustBe Some(trustedHelper)
    }

  }
}
