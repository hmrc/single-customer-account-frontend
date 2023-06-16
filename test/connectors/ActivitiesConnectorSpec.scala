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

import akka.http.scaladsl.model.StatusCodes.ServerError
import com.github.tomakehurst.wiremock.client.WireMock._
import fixtures.{SpecBase, WireMockHelper}
import models.integrationframework.{Activities, CapabilityDetails}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain
import uk.gov.hmrc.http.test.HttpClientSupport

import java.time.LocalDate


class ActivitiesConnectorSpec extends SpecBase with WireMockHelper with HttpClientSupport {

  import ActivitiesConnectorSpec._

  private lazy val activitiesConnector: ActivitiesConnector = injector.instanceOf[ActivitiesConnector]

  val activitiesResponseJson = Json.obj(
    "taxCalc" -> Json.arr(Json.obj(
      "nino" -> Json.obj(
        "hasNino" -> true,
        "nino" -> "GG012345C"
      ),
      "date" -> LocalDate.now.minusMonths(2).minusDays(1),
      "descriptionContent" -> "Your tax calculation for the 2022 to 2023 is now available",
      "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
      "activityHeading" -> "Your tax calculation"
    )),
    "taxCode" -> Json.arr(Json.obj(
      "nino" -> Json.obj(
        "hasNino" -> true,
        "nino" -> "GG012345C"
      ),
      "date" -> LocalDate.now.minusMonths(2).minusDays(1),
      "descriptionContent" -> "Your tax code has changed",
      "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
      "activityHeading" -> "Latest Tax code change"
    )),
    "childBenefit" -> Json.arr(Json.obj(
      "nino" -> Json.obj(
        "hasNino" -> true,
        "nino" -> "GG012345C"
      ),
      "date" -> LocalDate.now.minusMonths(2).minusDays(1),
      "descriptionContent" -> "HMRC paid you Child Benefit",
      "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
      "activityHeading" -> "Recent Child Benefit payments"
    ), Json.obj(
      "nino" -> Json.obj(
        "hasNino" -> true,
        "nino" -> "GG012345C"
      ),
      "date" -> LocalDate.now.minusMonths(4).minusDays(1),
      "descriptionContent" -> "HMRC paid you Child Benefit",
      "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
      "activityHeading" -> "Recent Child Benefit payments"
    )),
    "payeIncome" -> Json.arr(Json.obj(
      "nino" -> Json.obj(
        "hasNino" -> true,
        "nino" -> "GG012345C"
      ),
      "date" -> LocalDate.now.minusMonths(2).minusDays(1),
      "descriptionContent" -> "Your tax calculation for the 2022 to 2023 is now available",
      "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tasbt tetx-code-comparison",
      "activityHeading" -> "Your PAYE income for the current tax year55555"
    )))

  val emptyActivitiesResponseJson = Json.obj("taxCalc" -> Json.arr(), "taxCode" -> Json.arr(), "childBenefit" -> Json.arr(), "payeIncome" -> Json.arr())

  "Calling Activities Connector" must {
    "call getActivities and return successful response" in {

      server.stubFor(
        get(urlEqualTo(s"/single-customer-account-capabilities/activities/${nino}"))
          .willReturn(
            ok.withStatus(OK)
              .withHeader("Content-Type", "application/json")
              .withBody(
                emptyActivitiesResponseJson.toString())
          )
      )


      val result = activitiesConnector.getActivities(nino)
      whenReady(result) { result =>
        result mustBe emptyExpectedDetails
      }
    }

    "call getActivities and return NOT_FOUND response" in {

      server.stubFor(
        get(urlEqualTo(activitiesUrl))
          .willReturn(
            notFound.withStatus(NOT_FOUND)
              .withHeader("Content-Type", "application/json")
              .withBody(
                emptyActivitiesResponseJson.toString())
          )
      )

      val result = activitiesConnector.getActivities(nino)
      whenReady(result) { result =>
        result mustBe emptyExpectedDetails
      }
    }


    "call getActivities and return 5xx response" in {

      server.stubFor(
        get(urlEqualTo(activitiesUrl))
          .willReturn(
            serverError.withStatus(INTERNAL_SERVER_ERROR)
              .withHeader("Content-Type", "application/json")
              .withBody(
                emptyActivitiesResponseJson.toString())
          )
      )

      val result = activitiesConnector.getActivities(nino)
      whenReady(result) { result =>
        result mustBe emptyExpectedDetails
      }
    }

    "call getActivities and recover from exception" in {

      server.stubFor(
        get(urlEqualTo(activitiesUrl))
          .willReturn(
            badRequest
              .withHeader("Content-Type", "application/json")
              .withBody(
                "This is an exception"
              )
          )
      )

      val result = activitiesConnector.getActivities(nino)
      whenReady(result) { result =>
        result mustBe emptyExpectedDetails
      }
    }


  }
}

object ActivitiesConnectorSpec {

  val nino = domain.Nino("TT012345C")
  val ninoT = Nino(hasNino = true, nino = Some("TT012345C"))

  val expectedDetails = Activities(
    Seq(CapabilityDetails(
      ninoT,
      LocalDate.now.minusMonths(2).minusDays(1),
      "Your tax calculation for the 2022 to 2023 is now available",
      "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
      "Your tax calculation")),
    Seq(CapabilityDetails(
      ninoT,
      LocalDate.now.minusMonths(2).minusDays(1),
      "Your tax code has changed",
      "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
      "Latest Tax code change")),
    Seq(
      CapabilityDetails(
        ninoT,
        LocalDate.now.minusMonths(2).minusDays(1),
        "HMRC paid you Child Benefit",
        "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
        "Recent Child Benefit payments"),
      CapabilityDetails(
        ninoT,
        LocalDate.now.minusMonths(4).minusDays(1),
        "HMRC paid you Child Benefit",
        "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
        "Recent Child Benefit payments")
    ),
    Seq(CapabilityDetails(
      ninoT,
      LocalDate.now.minusMonths(2).minusDays(1),
      "Your tax calculation for the 2022 to 2023 is now available",
      "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
      "Your PAYE income for the current tax year")))

  val emptyExpectedDetails = Activities(Seq.empty, Seq.empty, Seq.empty, Seq.empty)


  private val activitiesUrl = s"/single-customer-account-capabilities/activities/$nino"
}
