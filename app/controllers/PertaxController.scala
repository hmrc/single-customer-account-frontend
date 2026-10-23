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

import connectors.PertaxConnector
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.partials.HtmlPartial
import views.html.templates.Layout

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.Try

class PertaxController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  pertaxConnector: PertaxConnector,
  layout: Layout
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with play.api.i18n.I18nSupport
    with Logging {

  private def errorViewReader(response: HttpResponse)(implicit request: MessagesRequest[AnyContent]) =
    errorView(response.body) match {
      case Some((partialPath, statusCode)) =>
        pertaxConnector.loadPartial(partialPath).map {
          case partial: HtmlPartial.Success =>
            Status(statusCode)(layout(pageTitle = partial.title.getOrElse(""))(partial.content))
          case partial: HtmlPartial.Failure =>
            logger.error(s"The partial $partialPath failed to be retrieved: $partial")
            InternalServerError
        }
      case None                            =>
        scala.concurrent.Future.successful(Status(response.status)(response.body))
    }

  def authorise: Action[AnyContent] = Action.async { implicit request =>
    pertaxConnector.authorise().flatMap(errorViewReader)
  }

  def checkHelpee(nino: String): Action[AnyContent] = Action.async { implicit request =>
    pertaxConnector.checkHelpee(nino).flatMap(errorViewReader)
  }

  private def errorView(responseBody: String): Option[(String, Int)] =
    Try(Json.parse(responseBody)).toOption.flatMap { json =>
      for {
        errorView  <- (json \ "errorView").toOption
        partialUrl <- (errorView \ "url").asOpt[String]
        statusCode <- (errorView \ "statusCode").asOpt[Int]
      } yield partialUrl -> statusCode
    }
}
