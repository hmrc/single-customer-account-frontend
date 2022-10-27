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

package controllers.auth

import controllers.actions.IFActionImpl
import fixtures.RetrievalOps.Ops
import fixtures.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel, CredentialStrength, Enrolments}

import scala.concurrent.Future

class AuthControllerSpec extends SpecBase{

  lazy val sessionRepositoryInstance = injector.instanceOf[SessionRepository]
  lazy val iFActionImplInstance = injector.instanceOf[IFActionImpl]
  lazy val controller: AuthController = new AuthController(messagesControllerComponents, frontendAppConfigInstance, sessionRepositoryInstance, iFActionImplInstance)
  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val nino = "AA999999A"
  def sessionClear : Future[Boolean] = Future.successful {
    true
  }
  "AuthController" must {
    "Redirect to government gateway sign-out link with correct continue url when signed in with government gateway with a continue URL" in {

      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(
        Some(nino) ~
          Individual ~
          Enrolments(fakeSaEnrolments("11111111", "Activated")) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.strong) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )

      val result = controller.signOut()(fakeRequest)
      redirectLocation(result).get should startWith("http://localhost:9025/gg/sign-out")
    }

    "Redirect to gg sign-in link with correct continue url when session ends" in {

      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(
        Some(nino) ~
          Individual ~
          Enrolments(fakeSaEnrolments("11111111", "Activated")) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.strong) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )

      val result = controller.signOutNoSurvey()(fakeRequest)
      redirectLocation(result).get should startWith("http://localhost:9025/gg/sign-out")
    }
  }
}
