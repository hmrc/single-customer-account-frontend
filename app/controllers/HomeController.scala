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

import config.FrontendAppConfig
import controllers.actions.{AuthAction, IFAction}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.sca.services.WrapperService
import views.html.HomeViewWrapperVersion

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class HomeController @Inject()(
                                val controllerComponents: MessagesControllerComponents,
                                authenticate: AuthAction,
                                getUserDetails: IFAction,
                                view: HomeViewWrapperVersion,
                                wrapperService: WrapperService
                              )(implicit frontendAppConfig: FrontendAppConfig, executionContext: ExecutionContext)
  extends FrontendBaseController with I18nSupport {

  def oldWrapperLayout: Action[AnyContent] = (authenticate andThen getUserDetails) { implicit request =>

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val name = request.ifData.details.name.fold("") { name => s"${name.firstForename.getOrElse("")} ${name.surname.getOrElse("")}" }

    Ok(
      wrapperService.layout(
        content = view(name),
        pageTitle = Some(Messages("page.title")),
        serviceNameUrl = Some(routes.HomeController.oldWrapperLayout.url),
        showSignOutInHeader = false,
        hideMenuBar = false,
        // showBackLinkJS = true,
        optTrustedHelper = request.authenticatedRequest.trustedHelper
      )
    )
  }

  def newWrapperLayout: Action[AnyContent] = (authenticate andThen getUserDetails) { implicit request =>

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val name = request.ifData.details.name.fold("") { name => s"${name.firstForename.getOrElse("")} ${name.surname.getOrElse("")}" }

    Ok(
      wrapperService.standardScaLayout(
        content = view(name),
        pageTitle = Some(Messages("page.title")),
        showSignOutInHeader = false,
        hideMenuBar = false,
        // showBackLinkJS = true,
        optTrustedHelper = request.authenticatedRequest.trustedHelper
      )
    )
  }

}
