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

import com.github.tomakehurst.wiremock.client.WireMock._
import fixtures.{SpecBase, WireMockHelper}
import models.integrationframework.{ActionDetails, Actions}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain
import uk.gov.hmrc.http.test.HttpClientSupport

import java.time.LocalDate


class ActionsConnectorSpec extends SpecBase with WireMockHelper with HttpClientSupport {

  import ActionsConnectorSpec._

  private lazy val actionsConnector: ActionsConnector = injector.instanceOf[ActionsConnector]

  val nino = domain.Nino("TT012345C")

  val actionsResponseJson = Json.obj("taxCalc" -> Json.arr(Json.obj(
    "nino" -> Nino(true, Some("GG012345C")),
    "date" -> LocalDate.now.minusMonths(2).minusDays(1),
    "descriptionContent" -> "You paid too much tax in the 2022 to 2023 tax year. HMRC owes you a £84.23 refund",
    "actionDescription" -> "Claim your tax refund",
    "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
    "activityHeading" -> "Things for you to do")))

  val emptyActionsResponseJson = Json.obj("taxCalc" -> Json.arr())

  "Calling Actions Connector" must {
    "call getActions and return successful response" in {

      server.stubFor(
        get(urlEqualTo(s"/single-customer-account-capabilities/actions/${nino}"))
          .willReturn(
            ok.withStatus(OK)
              .withHeader("Content-Type", "application/json")
              .withBody(
                emptyActionsResponseJson.toString())
          )
      )


      val result = actionsConnector.getActions(nino)
      whenReady(result) { result =>
        result mustBe emptyExpectedDetails
      }
    }

    "call getActions and return NOT_FOUND response" in {

      server.stubFor(
        get(urlEqualTo(actionsUrl))
          .willReturn(
            notFound.withStatus(NOT_FOUND)
              .withHeader("Content-Type", "application/json")
              .withBody(
                emptyActionsResponseJson.toString())
          )
      )

      val result = actionsConnector.getActions(nino)
      whenReady(result) { result =>
        result mustBe emptyExpectedDetails
      }
    }


    "call getActions and return 5xx response" in {

      server.stubFor(
        get(urlEqualTo(actionsUrl))
          .willReturn(
            serverError.withStatus(INTERNAL_SERVER_ERROR)
              .withHeader("Content-Type", "application/json")
              .withBody(
                emptyActionsResponseJson.toString())
          )
      )

      val result = actionsConnector.getActions(nino)
      whenReady(result) { result =>
        result mustBe emptyExpectedDetails
      }
    }

    "call getActions and recover from exception" in {

      server.stubFor(
        get(urlEqualTo(actionsUrl))
          .willReturn(
            badRequest
              .withHeader("Content-Type", "application/json")
              .withBody(
                "This is an exception"
              )
          )
      )

      val result = actionsConnector.getActions(nino)
      whenReady(result) { result =>
        result mustBe emptyExpectedDetails
      }
    }


  }
}


object ActionsConnectorSpec {

  val nino = domain.Nino("TT012345C")
  val ninoT = Nino(hasNino = true, nino = Some("TT012345C"))

  val expectedDetails = Actions(Seq(
    ActionDetails(
      nino = Nino(true, Some("GG012345C")),
      date = LocalDate.now.minusMonths(2).minusDays(1),
      descriptionContent = "You paid too much tax in the 2022 to 2023 tax year. HMRC owes you a £84.23 refund",
      actionDescription = "Claim your tax refund",
      url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
      activityHeading = "Things for you to do"),
  ))

  val emptyExpectedDetails = Actions(Seq.empty)


  private val actionsUrl = s"/single-customer-account-capabilities/actions/$nino"
}
