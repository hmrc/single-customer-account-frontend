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

import controllers.actions.{AuthAction, AuthActionImpl}
import fixtures.RetrievalOps._
import fixtures.SpecBase
import models.auth.AuthenticatedRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc._
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core._

import scala.concurrent.Future
import scala.language.postfixOps


class AuthActionSpec extends SpecBase {

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val nino = "AA999999A"

  class Harness(authAction: AuthAction) extends InjectedController {
    def onPageLoad: Action[AnyContent] = authAction { request: AuthenticatedRequest[AnyContent] =>
      Ok(
        s"Nino: ${request.nino.getOrElse("fail").toString}, Enrolments: ${request.enrolments.toString}," +
          s"trustedHelper: ${request.trustedHelper}, profileUrl: ${request.profile}"
      )
    }
  }

  def retrievals(
                  nino: Option[String] = None,
                  affinityGroup: AffinityGroup = Individual,
                  enrolments: Enrolments = Enrolments(Set.empty),
                  credentials: Option[Credentials] = Some(Credentials("id","type")),
                  credentialStrength: Option[String] = Some(CredentialStrength.strong),
                  confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200,
                  name: Option[Name] = None,
                  trustedHelper: Option[TrustedHelper] = None,
                  profileUrl: Option[String] = None
                ): Harness = {

    when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(
        nino ~
        affinityGroup ~
        enrolments ~
        credentials ~
        credentialStrength ~
        confidenceLevel ~
        name ~
        trustedHelper ~
        profileUrl
    )

    val action = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)

    new Harness(action)
  }

  "AuthAction" must {
    "allow an authenticated user into SCA with a NINO and Confidence Level 200" in {
      val controller = retrievals(nino = Some(nino))
      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "extract an SA UTR" in {
      val controller = retrievals(nino = Some(nino), enrolments = Enrolments(fakeSaEnrolments("11111111", "Activated")))
      val result = controller.onPageLoad()(fakeRequest)

      contentAsString(result) must include("11111111")
    }
  }

}
