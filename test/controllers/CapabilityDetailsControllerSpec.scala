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
import models.integrationframework.CapabilityDetails
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import play.api.http.Status.OK
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import services.CapabilityService
import uk.gov.hmrc.auth.core.Nino
import views.html.CapabilityDetailsView

import java.time.LocalDate
import scala.concurrent.Future

class CapabilityDetailsControllerSpec extends SpecBase with BeforeAndAfter {

  lazy val mockCapabilityService: CapabilityService = mock[CapabilityService]
  lazy val view: CapabilityDetailsView = inject[CapabilityDetailsView]

  lazy val controller: CapabilityDetailsController = new CapabilityDetailsController(messagesControllerComponents, mockCapabilityService, view)

  private def viewAsString(capabilityDetails: Seq[CapabilityDetails]) = view(capabilityDetails)(fakeRequest, messages).toString

  "CapabilityDetailsController" must {
    "Return the capabilityDetails page successfully with correct data" in {
      val capabilityDetails: Seq[CapabilityDetails] = Seq(
        CapabilityDetails(
          nino = Nino(true, Some("GG012345C")),
          date = LocalDate.of(2022, 5, 19),
          descriptionContent = "Desc-1",
          url = "url-1"),
        CapabilityDetails(
          nino = Nino(true, Some("GG012345C")),
          date = LocalDate.of(2023, 4, 9),
          descriptionContent = "Desc-2",
          url = "url-2")
      )

      when(mockCapabilityService.getCapabilityDetails(any())(any())).thenReturn(Future.successful(capabilityDetails))

      val result = controller.onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(capabilityDetails)

    }
  }
}
