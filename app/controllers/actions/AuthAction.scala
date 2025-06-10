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

package controllers.actions

import com.google.inject.{ImplementedBy, Inject}
import config.FrontendAppConfig
import connectors.FandFConnector
import controllers.routes
import models.auth.AuthenticatedRequest
import play.api.Logging
import play.api.mvc.*
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{Name, ~}
import uk.gov.hmrc.domain
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthActionImpl @Inject() (
  override val authConnector: AuthConnector,
  appConfig: FrontendAppConfig,
  val parser: BodyParsers.Default,
  fandFConnector: FandFConnector
)(implicit val executionContext: ExecutionContext)
    extends AuthorisedFunctions
    with AuthAction
    with Logging {

  private object GTOE200 {
    def unapply(confLevel: ConfidenceLevel): Option[ConfidenceLevel] =
      if (confLevel.level >= ConfidenceLevel.L200.level) Some(confLevel) else None
  }

  def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    authorised().retrieve(
      Retrievals.nino and
        Retrievals.affinityGroup and
        Retrievals.allEnrolments and
        Retrievals.credentials and
        Retrievals.credentialStrength and
        Retrievals.confidenceLevel and
        Retrievals.name
    ) {
      case nino ~ _ ~ Enrolments(enrolments) ~ Some(credentials) ~ Some(CredentialStrength.strong) ~
          GTOE200(confidenceLevel) ~ name =>
        val trimmedRequest: Request[A] = request
          .map {
            case AnyContentAsFormUrlEncoded(data) =>
              AnyContentAsFormUrlEncoded(data.map { case (key, vals) =>
                (key, vals.map(_.trim))
              })
            case b                                => b
          }
          .asInstanceOf[Request[A]]
        fandFConnector.getTrustedHelper().flatMap { trustedHelper =>
          val authenticatedRequest = AuthenticatedRequest[A](
            trustedHelper.flatMap(_.principalNino).orElse(nino).map(domain.Nino),
            credentials,
            confidenceLevel,
            Some(
              trustedHelper.fold(name.getOrElse(Name(None, None)))(helper => Name(Some(helper.principalName), None))
            ),
            trustedHelper,
            None,
            enrolments,
            trimmedRequest
          )
          logger.info(s"[AuthActionImpl][invokeBlock] Successful Auth request")
          block(authenticatedRequest)
        }

      case _ =>
        logger.info(s"[AuthActionImpl][invokeBlock] Unauthorised request")
        Future.successful(Redirect(routes.UnauthorisedController.onPageLoad))
    }
  }.recover { case authException =>
    logger.error(s"[AuthActionImpl][invokeBlock] exception: ${authException.getMessage}")
    Redirect(
      appConfig.loginUrl,
      Map("continue" -> Seq(appConfig.loginContinueUrl), "origin" -> Seq("single-customer-account-frontend"))
    )
  }
}

@ImplementedBy(classOf[AuthActionImpl])
trait AuthAction
    extends ActionBuilder[AuthenticatedRequest, AnyContent]
    with ActionFunction[Request, AuthenticatedRequest] {}
