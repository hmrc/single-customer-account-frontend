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

package controllers

import connectors.CapabilityConnector
import fixtures.SpecBase
import models.integrationframework.IfCapabilityDetails
import org.mockito.ArgumentMatchers._
import org.scalatest.BeforeAndAfter
import play.api.Application
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.inject.bind
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
//import org.mockito.Mockito.{verify, when}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{MessagesControllerComponents, Result}
import play.api.test.Helpers.{contentAsJson, contentAsString, defaultAwaitTimeout, status}
import services.CapabilityService
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain
import views.html.CapabilityDetailsView

import scala.concurrent.Future

class CapabilityDetailsControllerSpec extends SpecBase with BeforeAndAfter {

  lazy val capabilityService = mock[CapabilityService]
  lazy val capabilityDetailsView: CapabilityDetailsView = mock[CapabilityDetailsView]
  lazy val controller: CapabilityDetailsController = new CapabilityDetailsController(messagesControllerComponents,capabilityService, capabilityDetailsView)
  lazy val mockCapabilitiesConnector: CapabilityConnector = mock[CapabilityConnector]

  val nino = domain.Nino("GG012345C")
  val expectedDate = "9 April 2023"
  val expectedDescription = "Your tax code has changed"
  val expectedUrl = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"

  "CapabilityDetailsController" must {
    "Return the capabilityDetails page successfully with correct data" in {
      val capabilityDetails = IfCapabilityDetails(
        nino = Nino(true, Some("GG012345C")),
        date = "9 April 2023",
        descriptionContent = "Your tax code has changed",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"
      )

      when(capabilityService.getCapabilityDetails(any())(any())).thenReturn(Future.successful(capabilityDetails))

      val result = controller.getCapabilitiesData(fakeRequest)
      whenReady(result) { _ =>
        status(result) mustBe OK
        contentAsJson(result) mustBe Json.obj(
          "nino" -> Json.obj(
            "hasNino" -> true,
            "nino" -> "GG012345C"
          ),
          "date" -> "9 April 2023",
          "descriptionContent" -> "Your tax code has changed",
          "url" -> "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"
        )
      }
    }
  }
}
