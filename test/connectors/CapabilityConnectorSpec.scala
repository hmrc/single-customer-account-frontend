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
import models.integrationframework.CapabilityDetails
import play.api.libs.json.{JsArray, Json}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain

import java.time.LocalDate

class CapabilityConnectorSpec extends SpecBase with WireMockHelper {

  private lazy val capabilityConnector: CapabilityConnector = injector.instanceOf[CapabilityConnector]

  val nino = domain.Nino("GG012345C")
  val ninoT = Nino(hasNino = true, Some("GG012345C"))

  server.start()

  applicationBuilder.configure(
    "microservice.services.integration-framework.port" -> server.port(),
    "metrics.enabled" -> false,
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false
  ).build()

  "Calling CapabilityConnector" must {
    "call getCapabilityDetails and return successful response" in {

      val capabilitiesResponseJson: JsArray = Json.arr(
        Json.obj(
          "nino" -> Json.obj(
            "hasNino" -> true,
            "nino" -> "GG012345C"
          ),
          "date" -> "09-04-2021",
          "descriptionContent" -> "Your tax code has changed",
          "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
          "activityHeading" -> "Your Tax code has changed"
        )
      )
      val expectedDetails = CapabilityDetails(ninoT, LocalDate.of(2023, 4, 9), "Your tax code has changed", "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison", "Your Tax code has changed")


      server.stubFor(
        WireMock.get(urlEqualTo(s"/single-customer-account-capabilities/capabilities-data/GG012345C"))
          .willReturn(
            ok
              .withHeader("Content-Type", "application/json")
              .withBody(capabilitiesResponseJson.toString())
          )
      )

      capabilityConnector.getCapabilityDetails(nino).map { result =>

        result mustBe Some(expectedDetails)
      }
    }
  }
}
