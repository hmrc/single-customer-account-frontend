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

import com.github.tomakehurst.wiremock.client.WireMock._
import fixtures.WireMockHelper
import models.auth.{Data, EcoConnectorModel, GenerationMix, Intensity}
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class EcoConnectorSpec extends PlaySpec with Matchers with Injecting with WireMockHelper with PatienceConfiguration {
  implicit lazy val exContext: ExecutionContext                         = scala.concurrent.ExecutionContext.global
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
      |      }
      |      ]
      |    }]
      |  }]
      |}
      |""".stripMargin

  private implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val app: Application     = localGuiceApplicationBuilder().build()

  "get" must {
    "pass with valid start/end date and postcode - first set of dates/ postcode" in {
      val ec     = app.injector.instanceOf[EcoConnector]
      val expUrl = "/regional/intensity/2017-08-25T12:35Z/2017-08-25T12:35Z/postcode/NE34PL"

      server.stubFor(
        get(urlEqualTo(expUrl)).willReturn(
          ok(jsonResponse)
        )
      )
      val result = Await.result(
        ec.get(
          start = LocalDateTime.parse("2017-08-25T12:35Z", ISO_DATE_TIME),
          end = LocalDateTime.parse("2017-08-25T12:35Z", ISO_DATE_TIME),
          postcode = "NE34PL"
        ),
        Duration.Inf
      )

      result mustBe Right(
        Seq(
          EcoConnectorModel(
            regionid = 3,
            dnoregion = "Electricity North West",
            shortname = "North West England",
            postcode = "RG10",
            data = Seq(
              Data(
                from = LocalDateTime.parse("2018-01-20T12:00Z", ISO_DATE_TIME),
                to = LocalDateTime.parse("2018-01-20T12:30Z", ISO_DATE_TIME),
                intensity = Intensity(
                  forecast = 266,
                  index = "moderate"
                ),
                generationmix = Seq(
                  GenerationMix(
                    fuel = "gas",
                    perc = BigDecimal(43.6)
                  ),
                  GenerationMix(
                    fuel = "coal",
                    perc = BigDecimal(0.7)
                  )
                )
              )
            )
          )
        )
      )
      server.verify(
        getRequestedFor(
          urlEqualTo(
            expUrl
          )
        )
      )
    }

    "pass with valid start/end date and postcode - second set of dates/ postcode" in {
      val ec     = app.injector.instanceOf[EcoConnector]
      val expUrl = "/regional/intensity/2019-08-25T12:35Z/2020-08-25T12:35Z/postcode/NE164TQ"

      server.stubFor(
        get(urlEqualTo(expUrl)).willReturn(
          ok(jsonResponse)
        )
      )
      val result = Await.result(
        ec.get(
          start = LocalDateTime.parse("2019-08-25T12:35Z", ISO_DATE_TIME),
          end = LocalDateTime.parse("2020-08-25T12:35Z", ISO_DATE_TIME),
          postcode = "NE164TQ"
        ),
        Duration.Inf
      )

      result mustBe Right(
        Seq(
          EcoConnectorModel(
            regionid = 3,
            dnoregion = "Electricity North West",
            shortname = "North West England",
            postcode = "RG10",
            data = Seq(
              Data(
                from = LocalDateTime.parse("2018-01-20T12:00Z", ISO_DATE_TIME),
                to = LocalDateTime.parse("2018-01-20T12:30Z", ISO_DATE_TIME),
                intensity = Intensity(
                  forecast = 266,
                  index = "moderate"
                ),
                generationmix = Seq(
                  GenerationMix(
                    fuel = "gas",
                    perc = BigDecimal(43.6)
                  ),
                  GenerationMix(
                    fuel = "coal",
                    perc = BigDecimal(0.7)
                  )
                )
              )
            )
          )
        )
      )
      server.verify(
        getRequestedFor(
          urlEqualTo(
            expUrl
          )
        )
      )
    }

    "fail correctly" in {
      val ec     = app.injector.instanceOf[EcoConnector]
      val expUrl = "/regional/intensity/2019-08-25T12:35Z/2020-08-25T12:35Z/postcode/NE164TQ"

      server.stubFor(
        get(urlEqualTo(expUrl)).willReturn(
          serverError()
        )
      )
      val result = Await.result(
        ec.get(
          start = LocalDateTime.parse("2019-08-25T12:35Z", ISO_DATE_TIME),
          end = LocalDateTime.parse("2020-08-25T12:35Z", ISO_DATE_TIME),
          postcode = "NE164TQ"
        ),
        Duration.Inf
      )

      result.isLeft mustBe true
      result.swap.map(_.statusCode) mustBe Right(500)

      server.verify(
        getRequestedFor(
          urlEqualTo(
            expUrl
          )
        )
      )
    }

  }
}
