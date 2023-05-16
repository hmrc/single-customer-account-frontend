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

import fixtures.SpecBase
import models.integrationframework.IfCapabilityDetails
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import play.api.http.Status.OK
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import services.CapabilityService
import uk.gov.hmrc.auth.core.Nino
import views.html.CapabilityDetailsView

import scala.concurrent.Future

class CapabilityDetailsControllerSpec extends SpecBase with BeforeAndAfter {

  lazy val mockCapabilityService = mock[CapabilityService]
  lazy val view: CapabilityDetailsView = inject[CapabilityDetailsView]

  lazy val controller: CapabilityDetailsController = new CapabilityDetailsController(messagesControllerComponents, mockCapabilityService, view)

  private def viewAsString(date: String, desc: String, url: String) = view(date, desc, url)(fakeRequest, messages).toString

  "CapabilityDetailsController" must {
    "Return the capabilityDetails page successfully with correct data" in {
      val capabilityDetails = IfCapabilityDetails(
        nino = Nino(true, Some("GG012345C")),
        date = "9 April 2023",
        descriptionContent = "Your tax code has changed",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"
      )

      when(mockCapabilityService.getCapabilityDetails(any())(any())).thenReturn(Future.successful(capabilityDetails))

      val result = controller.onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(capabilityDetails.date, capabilityDetails.descriptionContent, capabilityDetails.url)

    }
  }
}
