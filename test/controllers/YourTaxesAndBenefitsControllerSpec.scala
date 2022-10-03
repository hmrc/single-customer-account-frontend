/*
 * Copyright 2022 HM Revenue & Customs
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
import fixtures.RetrievalOps._
import fixtures.SpecBase
import org.mockito.ArgumentMatchers.{any, endsWith}
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel, CredentialStrength, Enrolments}
import views.html.YourTaxesAndBenefits

import scala.concurrent.Future

class YourTaxesAndBenefitsControllerSpec extends SpecBase {

  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)
  lazy val view: YourTaxesAndBenefits = injector.instanceOf[YourTaxesAndBenefits]
  lazy val controller: YourTaxesAndBenefitsController = new YourTaxesAndBenefitsController(messagesControllerComponents, authAction, ifActionInstance, view)
  val nino = "AA999999A"

  "YourTaxesAndBenefitsController" must {
    "Return the Tax and Benefit Page" in {
      when(mockAuthConnector.authorise[AuthRetrievals](any(),any())(any(), any())) thenReturn Future.successful(
        Some(nino) ~
          Individual ~
          Enrolments(fakeSaEnrolments("11111111","Activated")) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.strong) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )

      val result = controller.onPageLoad(FakeRequest().withSession("sessionId" -> "FAKE_SESSION_ID"))

      contentAsString(result) should include("Your taxes and benefits")
      contentAsString(result) should include("Self Assessment")
      whenReady(result) { res =>
        res.header.status shouldBe 200
      }
    }

    "Return the Unauthorised Page given weak credentials" in {
      when(mockAuthConnector.authorise[AuthRetrievals](any(),any())(any(), any())) thenReturn Future.successful(

        Some(nino) ~
          Individual ~
          Enrolments(fakeSaEnrolments("11111111", "Activated")) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.weak) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )

      val result = controller.onPageLoad(FakeRequest().withSession("sessionId" -> "FAKE_SESSION_ID"))

      redirectLocation(result).get should endWith("/single-customer-account/unauthorised")
      whenReady(result) { res =>
        res.header.status shouldBe 303
      }
    }

    "Return the Tax and Benefit with no SA link when SA not enrolled" in {
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(

        Some(nino) ~
          Individual ~
          Enrolments(Set.empty) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.strong) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )

      val result = controller.onPageLoad(FakeRequest().withSession("sessionId" -> "FAKE_SESSION_ID"))

      contentAsString(result) should not include("Self Assessment")
      whenReady(result) { res =>
        res.header.status shouldBe 200
      }
    }

    "Return the Tax and Benefit with no SA link when SA not activated" in {
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(

        Some(nino) ~
          Individual ~
          Enrolments(fakeSaEnrolments("11111111","NotYetActivated")) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.strong) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )

      val result = controller.onPageLoad(FakeRequest().withSession("sessionId" -> "FAKE_SESSION_ID"))

      contentAsString(result) should not include ("Self Assessment")
      whenReady(result) { res =>
        res.header.status shouldBe 200
      }
    }

  }

}
