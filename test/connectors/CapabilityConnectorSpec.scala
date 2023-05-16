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
import models.integrationframework.IfCapabilityDetails
import play.api.libs.json.Json
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain

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

      val expectedDetails = IfCapabilityDetails(ninoT, "9 April 2023", "Your tax code has changed", "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison")

      server.stubFor(
        WireMock.get(urlEqualTo(s"/single-customer-account-capabilities/capabilities-data/${nino.value}"))
          .willReturn(
            ok
              .withHeader("Content-Type", "application/json")
              .withBody(Json.obj(
                "nino" -> Json.obj(
                  "hasNino" -> true,
                  "nino" -> "GG012345C"
                ),
                "date" -> "9 April 2023",
                "descriptionContent" -> "Your tax code has changed",
                "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"
              ).toString())
          )
      )

      capabilityConnector.getCapabilityDetails(nino).map { result =>
        result mustBe Some(expectedDetails)
      }
    }
  }
}
