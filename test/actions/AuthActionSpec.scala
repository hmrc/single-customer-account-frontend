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

package actions

import connectors.FandFConnector
import controllers.actions.{AuthAction, AuthActionImpl}
import fixtures.RetrievalOps.*
import fixtures.SpecBase
import models.auth.AuthenticatedRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import play.api.mvc.*
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}

import scala.concurrent.Future

class AuthActionSpec extends SpecBase with BeforeAndAfterEach {
  private val mockFandFConnector: FandFConnector = mock[FandFConnector]
  val mockAuthConnector: AuthConnector           = mock[AuthConnector]
  val nino                                       = "AA999999A"

  class Harness(authAction: AuthAction) extends InjectedController {
    def onPageLoad: Action[AnyContent] = authAction { (request: AuthenticatedRequest[AnyContent]) =>
      Ok(
        s"Nino: ${request.nino.getOrElse("fail").toString}, Enrolments: ${request.enrolments.toString}," +
          s"trustedHelper: ${request.trustedHelper}, profileUrl: ${request.profile}"
      )
    }
  }

  def retrievals(
    nino: Option[String] = None,
    affinityGroup: Option[AffinityGroup] = Some(Individual),
    enrolments: Enrolments = Enrolments(Set.empty),
    credentials: Option[Credentials] = Some(Credentials("id", "type")),
    credentialStrength: Option[String] = Some(CredentialStrength.strong),
    confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200,
    name: Option[Name] = None,
    trustedHelper: Option[TrustedHelper] = None
  ): Harness = {

    when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(
      nino ~
        affinityGroup ~
        enrolments ~
        credentials ~
        credentialStrength ~
        confidenceLevel ~
        name
    )

    val action =
      new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance, mockFandFConnector)

    new Harness(action)
  }

  override def beforeEach(): Unit = {
    reset(mockFandFConnector)
    reset(mockAuthConnector)
    ()
  }

  "AuthAction" must {
    "allow an authenticated user into SCA with a NINO and Confidence Level 200" in {
      when(mockFandFConnector.getTrustedHelper()(any())).thenReturn(Future.successful(None))
      val controller = retrievals(nino = Some(nino))
      val result     = controller.onPageLoad()(fakeRequest)

      status(result) mustBe OK
      verify(mockFandFConnector, times(1)).getTrustedHelper()(any())
    }
    "not get trusted helper when unauthorised" in {
      when(mockFandFConnector.getTrustedHelper()(any())).thenReturn(Future.successful(None))

      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.failed(
        MissingBearerToken("error")
      )

      val action =
        new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance, mockFandFConnector)

      val controller = new Harness(action)

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      verify(mockFandFConnector, never()).getTrustedHelper()(any())
    }

    "allow an authenticated user into SCA with a NINO and Confidence Level 50 - don't call fandf connector" in {
      when(mockFandFConnector.getTrustedHelper()(any())).thenReturn(Future.successful(None))
      val controller = retrievals(nino = Some(nino), confidenceLevel = ConfidenceLevel.L50)
      val result     = controller.onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      verify(mockFandFConnector, never()).getTrustedHelper()(any())
    }

    "extract an SA UTR" in {
      when(mockFandFConnector.getTrustedHelper()(any())).thenReturn(Future.successful(None))
      val controller = retrievals(nino = Some(nino), enrolments = Enrolments(fakeSaEnrolments("11111111", "Activated")))
      val result     = controller.onPageLoad()(fakeRequest)
      contentAsString(result) must include("11111111")
    }
  }

}
