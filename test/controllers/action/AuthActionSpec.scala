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

package controllers.action

import connectors.FandFConnector
import controllers.action.AuthActionSpec.{Harness, authRetrievals, emptyAuthRetrievals, erroneousRetrievals, fakeAuthConnector}
import controllers.actions.{AuthAction, AuthActionImpl}
import fixtures.RetrievalOps.Ops
import fixtures.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.OK
import play.api.mvc.*
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase with BeforeAndAfterEach with MockitoSugar {
  private val mockFandFConnector: FandFConnector = mock[FandFConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockFandFConnector)
    when(mockFandFConnector.getTrustedHelper()(any()))
      .thenReturn(Future.successful(Some(TrustedHelper("name", "name", "link", Some("AA999999A")))))

    ()
  }

  "the user has valid credentials incl trusted helper"   must {
    "return OK" in {
      val authAction =
        new AuthActionImpl(
          fakeAuthConnector(authRetrievals),
          frontendAppConfigInstance,
          AuthActionSpec.parser,
          mockFandFConnector
        )
      val controller = new Harness(authAction)

      val result = controller.onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result).contains("AA999999A") mustBe true
      verify(mockFandFConnector, times(1)).getTrustedHelper()(any())
    }
  }
  "the user has valid credentials and no trusted helper" must {
    "return OK" in {
      when(mockFandFConnector.getTrustedHelper()(any()))
        .thenReturn(Future.successful(None))
      val authAction =
        new AuthActionImpl(
          fakeAuthConnector(authRetrievals),
          frontendAppConfigInstance,
          AuthActionSpec.parser,
          mockFandFConnector
        )
      val controller = new Harness(authAction)

      val result = controller.onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result).isEmpty mustBe true
      verify(mockFandFConnector, times(1)).getTrustedHelper()(any())
    }
  }

  "the user does not have valid enrolments" must {
    "redirect to unauthorised" in {
      val authAction =
        new AuthActionImpl(
          fakeAuthConnector(emptyAuthRetrievals),
          frontendAppConfigInstance,
          AuthActionSpec.parser,
          mockFandFConnector
        )
      val controller = new Harness(authAction)

      val result = controller.onPageLoad()(fakeRequest)
      status(result) mustBe SEE_OTHER
    }
  }
  "the user does not have valid id"         must {
    "redirect to unauthorised" in {
      val authAction =
        new AuthActionImpl(
          fakeAuthConnector(erroneousRetrievals),
          frontendAppConfigInstance,
          AuthActionSpec.parser,
          mockFandFConnector
        )
      val controller = new Harness(authAction)

      val result = controller.onPageLoad()(fakeRequest)
      status(result) mustBe SEE_OTHER
    }
  }

  "the user hasn't logged in" must {
    "redirect the user to log in " in {
      val authAction = new AuthActionImpl(
        fakeAuthConnector(Future.failed(new MissingBearerToken)),
        frontendAppConfigInstance,
        AuthActionSpec.parser,
        mockFandFConnector
      )
      val controller = new Harness(authAction)
      val result     = controller.onPageLoad()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result).get must startWith(frontendAppConfigInstance.loginUrl)
    }
  }

  "the user's session has expired" must {
    "redirect the user to log in " in {
      val authAction = new AuthActionImpl(
        fakeAuthConnector(Future.failed(new BearerTokenExpired)),
        frontendAppConfigInstance,
        AuthActionSpec.parser,
        mockFandFConnector
      )
      val controller = new Harness(authAction)
      val result     = controller.onPageLoad()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result).get must startWith(frontendAppConfigInstance.loginUrl)
    }
  }
}

object AuthActionSpec extends SpecBase with MockitoSugar {

  val nino = "AA999999A"

  private def fakeAuthConnector(stubbedRetrievalResult: Future[_]): AuthConnector =
    new AuthConnector {
      def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
        hc: HeaderCarrier,
        ec: ExecutionContext
      ): Future[A] =
        stubbedRetrievalResult.map(_.asInstanceOf[A])(ec)
    }

  val authRetrievals: Future[
    Some[String] ~ Some[AffinityGroup] ~ Enrolments ~ Some[Credentials] ~ Some[String] ~ ConfidenceLevel.L200.type ~
      Some[Name]
  ] = Future.successful(
    Some(nino) ~
      Some(Individual) ~
      Enrolments(fakeSaEnrolments("11111111", "Activated")) ~
      Some(Credentials("id", "type")) ~
      Some(CredentialStrength.strong) ~
      ConfidenceLevel.L200 ~
      Some(Name(Some("chaz"), Some("dingle")))
  )

  private def emptyAuthRetrievals: Future[Some[String] ~ Enrolments] =
    Future.successful(new ~(Some("id"), Enrolments(Set())))

  private def erroneousRetrievals: Future[None.type ~ Enrolments] =
    Future.successful(new ~(None, Enrolments(Set())))

  class Harness(
    authAction: AuthAction,
    val controllerComponents: MessagesControllerComponents = messagesControllerComponents
  ) extends BaseController {
    def onPageLoad(): Action[AnyContent] =
      authAction.apply(e => Ok(s"${e.trustedHelper.map(_.principalNino.getOrElse("")).getOrElse("")}"))
  }

  private val parser: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]
}
