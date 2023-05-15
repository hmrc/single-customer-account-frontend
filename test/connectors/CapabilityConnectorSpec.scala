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

import com.github.tomakehurst.wiremock.client.WireMock.{get, ok, urlEqualTo}
import controllers.actions.AuthActionImpl
import fixtures.{SpecBase, WireMockHelper}
import models.integrationframework.IfCapabilityDetails
import uk.gov.hmrc.auth.core.{AuthConnector, Nino}
import uk.gov.hmrc.domain

class CapabilityConnectorSpec extends SpecBase with WireMockHelper {
  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)
  lazy val connector = injector.instanceOf[CapabilityConnector]

  val nino = domain.Nino("GG012345C")
  val ninoT = Nino(hasNino = true,Some("GG012345C"))

  server.start()

  applicationBuilder.configure(
    "microservice.services.integration-framework.port" -> server.port(),
    "metrics.enabled" -> false,
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false
  )
    .build()

  "Calling CapabilityConnector" must {
    "call getCapabilityDetails and return successful response" in {

      val expectedDetails = IfCapabilityDetails(ninoT, "9 April 2023", "Your tax code has changed", "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison")

      val result = connector.getCapabilityDetails(nino)
      result.futureValue mustBe Some(expectedDetails)
    }
  }
}
