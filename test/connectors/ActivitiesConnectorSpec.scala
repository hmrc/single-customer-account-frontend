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

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{ok, urlEqualTo}
import fixtures.{SpecBase, WireMockHelper}
import models.integrationframework.{Activities, CapabilityDetails}
import play.api.libs.json.Json
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain

import java.time.LocalDate

class ActivitiesConnectorSpec extends SpecBase with WireMockHelper {

  private lazy val capabilityConnector: ActivitiesConnector = injector.instanceOf[ActivitiesConnector]

  val nino = domain.Nino("TT012345C")
  val ninoT = Nino(hasNino = true, Some("TT012345C"))

  server.start()

  applicationBuilder.configure(
    "microservice.services.integration-framework.port" -> server.port(),
    "metrics.enabled" -> false,
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false
  ).build()

  "Calling Activities Connector" must {
    "call getActivityDetails and return successful response" in {


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


      server.stubFor(
        WireMock.get(urlEqualTo(s"/single-customer-account-capabilities/activities/${nino}"))
          .willReturn(
            ok
              .withHeader("Content-Type", "application/json")
              .withBody(
                Json.obj("taxCalc" -> Json.arr(Json.obj(
                  "nino" -> Json.obj(
                    "hasNino" -> true,
                    "nino" -> "TT012345C"
                  ),
                  "date" -> LocalDate.now.minusMonths(2).minusDays(1),
                  "descriptionContent" -> "Your tax calculation for the 2022 to 2023 is now available",
                  "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
                  "activityHeading" -> "Your tax calculation"
                )),
                  "taxCode" -> Json.arr(Json.obj(
                    "nino" -> Json.obj(
                      "hasNino" -> true,
                      "nino" -> "TT012345C"
                    ),
                    "date" -> LocalDate.now.minusMonths(2).minusDays(1),
                    "descriptionContent" -> "Your tax code has changed",
                    "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
                    "activityHeading" -> "Latest Tax code change"
                  )),
                  "childBenefit" -> Json.arr(Json.obj(
                    "nino" -> Json.obj(
                      "hasNino" -> true,
                      "nino" -> "TT012345C"
                    ),
                    "date" -> LocalDate.now.minusMonths(2).minusDays(1),
                    "descriptionContent" -> "HMRC paid you Child Benefit",
                    "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
                    "activityHeading" -> "Recent Child Benefit payments"
                  ), Json.obj(
                    "nino" -> Json.obj(
                      "hasNino" -> true,
                      "nino" -> "TT012345C"
                    ),
                    "date" -> LocalDate.now.minusMonths(4).minusDays(1),
                    "descriptionContent" -> "HMRC paid you Child Benefit",
                    "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
                    "activityHeading" -> "Recent Child Benefit payments"
                  )),
                  "payeIncome" -> Json.arr(Json.obj(
                    "nino" -> Json.obj(
                      "hasNino" -> true,
                      "nino" -> "TT012345C"
                    ),
                    "date" -> LocalDate.now.minusMonths(2).minusDays(1),
                    "descriptionContent" -> "Your tax calculation for the 2022 to 2023 is now available",
                    "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
                    "activityHeading" -> "Your PAYE income for the current tax year"
                  ))).toString())
          )
      )


      whenReady(capabilityConnector.getActivityDetails(nino)) { result =>
        println("Result: " + result)
        println("expectedDetails: " + expectedDetails)
        result mustBe Some(expectedDetails)
      }
    }
  }
}
