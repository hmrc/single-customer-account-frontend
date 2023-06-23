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

import controllers.actions.AuthActionImpl
import fixtures.SpecBase
import models.integrationframework.{Activities, CapabilityDetails}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import play.api.http.Status.OK
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import services.ActivitiesService
import uk.gov.hmrc.auth.core.{AuthConnector, Nino}
import views.html.ActivitiesView

import java.time.LocalDate
import scala.concurrent.Future


class ActivitiesControllerSpec extends SpecBase with BeforeAndAfter {

  lazy val mockActivitiesService: ActivitiesService = mock[ActivitiesService]
  lazy val view: ActivitiesView = inject[ActivitiesView]
  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)

  lazy val controller: ActivitiesController = new ActivitiesController(messagesControllerComponents, mockActivitiesService, authActionInstance, ifActionInstance, view)

  private def viewAsString(Activities: Activities) = view(Activities)(fakeRequest, messages).toString

  "ActivitiesController" must {
    "Return the activities page successfully with correct data" in {
      val activities: Activities = Activities(Seq(
        CapabilityDetails(
          nino = Nino(true, Some("GG012345C")),
          date = LocalDate.of(2022, 5, 19),
          descriptionContent = "Desc-1",
          url = "url-1",
          activityHeading = "Your Tax calculation"),
        CapabilityDetails(
          nino = Nino(true, Some("GG012345C")),
          date = LocalDate.of(2023, 4, 9),
          descriptionContent = "Desc-2",
          url = "url-2",
          activityHeading = "Your Tax calculation")
      ), Seq.empty, Seq.empty, Seq.empty)

      when(mockActivitiesService.getActivities(any())(any())).thenReturn(Future.successful(activities))

      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe OK

      contentAsString(result).replace("%2F", "") mustBe viewAsString(activities).replace("%2F", "")
    }
  }
}
