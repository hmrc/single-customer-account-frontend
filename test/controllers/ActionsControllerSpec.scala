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
import models.integrationframework.{ActionDetails, Actions, CapabilityDetails}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import play.api.http.Status.OK
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import services.ActionsService
import uk.gov.hmrc.auth.core.{AuthConnector, Nino}
import views.html.ActionsView

import java.time.LocalDate
import scala.concurrent.Future


class ActionsControllerSpec extends SpecBase with BeforeAndAfter {

  lazy val mockActionsService: ActionsService = mock[ActionsService]
  lazy val view: ActionsView = inject[ActionsView]
  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)

  lazy val controller: ActionsController = new ActionsController(messagesControllerComponents, mockActionsService, authActionInstance, ifActionInstance, view)

  private def viewAsString(Actions: Actions) = view(Actions)(fakeRequest, messages).toString

  "ActionsController" must {
    "Return the actions page successfully with correct data" in {
      val actions: Actions = Actions(Seq(
        ActionDetails(
          nino = Nino(true, Some("GG012345C")),
          date = LocalDate.now.minusMonths(2).minusDays(1),
          descriptionContent = "You paid too much tax in the 2022 to 2023 tax year. HMRC owes you a £84.23 refund",
          actionDescription = "Claim your tax refund",
          url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
          activityHeading = "Things for you to do"),
      ))

      when(mockActionsService.getActions(any())(any())).thenReturn(Future.successful(actions))

      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe OK

//      contentAsString(result).replace("%2F", "") mustBe viewAsString(actions).replace("%2F", "")
    }
  }
}
