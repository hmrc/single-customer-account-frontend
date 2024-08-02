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

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, getRequestedFor, ok, urlEqualTo}
import fixtures.WireMockHelper
import models.auth.EcoConnectorModel
import org.scalatest.concurrent.Futures.whenReady
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, ok, post, urlEqualTo}

class EcoConnectorSpec extends PlaySpec with Matchers with Injecting with WireMockHelper with PatienceConfiguration {
  protected def localGuiceApplicationBuilder(): GuiceApplicationBuilder =
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.carbonintensityBaseUrl" -> server.baseUrl()
      )

  private val jsonResponse =
    """
      |{
      |  "data":[
      |  {
      |    "regionid": 3,
      |    "dnoregion": "Electricity North West",
      |    "shortname": "North West England",
      |    "postcode": "RG10",
      |    "data":[
      |    {
      |      "from": "2018-01-20T12:00Z",
      |      "to": "2018-01-20T12:30Z",
      |      "intensity": {
      |        "forecast": 266,
      |        "index": "moderate"
      |      },
      |      "generationmix": [
      |      {
      |        "fuel": "gas",
      |        "perc": 43.6
      |      },
      |      {
      |        "fuel": "coal",
      |        "perc": 0.7
      |      },
      |      {
      |        "fuel": "biomass",
      |        "perc": 4.2
      |      },
      |      {
      |        "fuel": "nuclear",
      |        "perc": 17.6
      |      },
      |      {
      |        "fuel": "hydro",
      |        "perc": 2.2
      |      },
      |      {
      |        "fuel": "imports",
      |        "perc": 6.5
      |      },
      |      {
      |        "fuel": "other",
      |        "perc": 0.3
      |      },
      |      {
      |        "fuel": "wind",
      |        "perc": 6.8
      |      },
      |      {
      |        "fuel": "solar",
      |        "perc": 18.1
      |      }
      |      ]
      |    }]
      |  }]
      |}
      |""".stripMargin

  private implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val app: Application     = localGuiceApplicationBuilder().build()
  "get" must {
    "assert xxx" in {
      val ec     = app.injector.instanceOf[EcoConnector]
      val expUrl = "/regional/intensity/2017-08-25T12:35Z/2017-08-25T12:35Z/postcode/NE34PL"

      server.stubFor(
        get(urlEqualTo(expUrl)).willReturn(
          ok(jsonResponse)
        )
      )
      val result = Await.result(ec.get("2017-08-25T12:35Z", "2017-08-25T12:35Z", "NE34PL"), Duration.Inf)

      result mustBe EcoConnectorModel(Nil, "moderate")
      server.verify(
        getRequestedFor(
          urlEqualTo(
            expUrl
          )
        )
      )

      // result mustBe EcoConnectorModel(Nil, "moderate")

    }
  }
}
