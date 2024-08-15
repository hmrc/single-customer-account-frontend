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

import org.scalatest.matchers.must.Matchers
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import com.github.tomakehurst.wiremock.client.WireMock._

import scala.concurrent.ExecutionContext
import fixtures.WireMockHelper
import models.carbonintensity.{CarbonIntensityData, CarbonIntensityDetails, GenerationMix, Intensity}
import org.scalatestplus.play.PlaySpec
import play.api.http.Status.{BAD_REQUEST, IM_A_TEAPOT, INTERNAL_SERVER_ERROR}

import java.time.LocalDateTime

class CarbonIntensityDataConnectorSpec extends PlaySpec with Matchers with ScalaFutures with WireMockHelper {

  implicit lazy val exContext: ExecutionContext                         = scala.concurrent.ExecutionContext.global
  implicit val hc: HeaderCarrier                                        = HeaderCarrier()
  protected def localGuiceApplicationBuilder(): GuiceApplicationBuilder =
    GuiceApplicationBuilder().configure("urls.carbon-intensity.base" -> server.baseUrl())

  implicit lazy val app: Application                                    = localGuiceApplicationBuilder().build()

  lazy val carbonIntensityDataConnector: CarbonIntensityDataConnector =
    app.injector.instanceOf[CarbonIntensityDataConnector]

  val fromDate: String           = "2024-03-20T12:00Z"
  val toDate: String             = "2024-03-20T12:30Z"
  val postcode: String           = "RG10"
  val carbonIntensityUrl: String = s"/regional/intensity/$fromDate/$toDate/postcode/$postcode"

  private val jsonResponse: String =
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
      |      "from": "2024-03-20T12:00Z",
      |      "to": "2024-03-20T12:30Z",
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
      |        "perc": 3.4
      |      },
      |      {
      |        "fuel": "nuclear",
      |        "perc": 17.6
      |      }
      |      ]
      |    }]
      |  }]
      |}
      |""".stripMargin

  "getCarbonIntensityData" must {
    "return Seq[CarbonIntensityDetails] when called with valid fromDate, toDate and postcode" in {

      val fromDate: LocalDateTime = LocalDateTime.of(2024, 3, 20, 12, 0)
      val toDate: LocalDateTime   = LocalDateTime.of(2024, 3, 20, 12, 30)

      val expectedCarbonIntensityData: Seq[CarbonIntensityDetails] = Seq(
        CarbonIntensityDetails(
          regionid = 3,
          dnoregion = "Electricity North West",
          shortname = "North West England",
          postcode = "RG10",
          data = Seq(
            CarbonIntensityData(
              from = LocalDateTime.of(2024, 3, 20, 12, 0),
              to = LocalDateTime.of(2024, 3, 20, 12, 30),
              intensity = Intensity(
                forecast = 266,
                index = "moderate"
              ),
              generationmix = Seq(
                GenerationMix(fuel = "gas", perc = BigDecimal(43.6)),
                GenerationMix(fuel = "coal", perc = BigDecimal(0.7)),
                GenerationMix(fuel = "biomass", perc = BigDecimal(3.4)),
                GenerationMix(fuel = "nuclear", perc = BigDecimal(17.6))
              )
            )
          )
        )
      )

      server.stubFor(
        get(urlEqualTo(carbonIntensityUrl)).willReturn(ok(jsonResponse))
      )

      val result = carbonIntensityDataConnector.getCarbonIntensityData(fromDate, toDate, postcode).value.futureValue

      result mustBe a[Right[_, Seq[CarbonIntensityDetails]]]
      result.getOrElse(Seq.empty[CarbonIntensityDetails]) mustBe expectedCarbonIntensityData

      server.verify(getRequestedFor(urlEqualTo(carbonIntensityUrl)))
    }

    "return Left(UpstreamErrorResponse) when API returns 400 for longer date range " in {

      val from: LocalDateTime        = LocalDateTime.of(2024, 1, 1, 10, 30)
      val to: LocalDateTime          = LocalDateTime.of(2024, 6, 28, 10, 45)
      val urlWithLongerRange: String = "/regional/intensity/2024-01-01T10:30Z/2024-06-28T10:45Z/postcode/RG10"

      server.stubFor(
        get(urlEqualTo(urlWithLongerRange)).willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      val result = carbonIntensityDataConnector.getCarbonIntensityData(from, to, postcode).value.futureValue

      result mustBe a[Left[UpstreamErrorResponse, _]]
      result.swap.getOrElse(UpstreamErrorResponse("", IM_A_TEAPOT)).statusCode mustBe BAD_REQUEST
    }

    "return Left(UpstreamErrorResponse) when API returns 500" in {

      val from: LocalDateTime = LocalDateTime.of(2024, 3, 20, 12, 0)
      val to: LocalDateTime   = LocalDateTime.of(2024, 3, 20, 12, 30)

      server.stubFor(
        get(urlEqualTo(carbonIntensityUrl)).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      val result = carbonIntensityDataConnector.getCarbonIntensityData(from, to, postcode).value.futureValue

      result mustBe a[Left[UpstreamErrorResponse, _]]
      result.swap.getOrElse(UpstreamErrorResponse("", IM_A_TEAPOT)).statusCode mustBe INTERNAL_SERVER_ERROR
    }
  }
}
