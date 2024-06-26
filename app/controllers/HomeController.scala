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

import controllers.actions.AuthAction
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.hmrcstandardpage.ServiceURLs
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.sca.services.WrapperService
import views.html.HomeViewWrapperVersion

import javax.inject.Inject

class HomeController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  authenticate: AuthAction,
  view: HomeViewWrapperVersion,
  wrapperService: WrapperService
) extends FrontendBaseController
    with I18nSupport {

  def oldWrapperLayout: Action[AnyContent] = authenticate { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    Ok(
      wrapperService.layout(
        content = view(""),
        pageTitle = Some(Messages("page.title")),
        serviceNameUrl = Some(routes.HomeController.oldWrapperLayout.url),
        hideMenuBar = false,
        signoutUrl = Some("/logout"),
        optTrustedHelper = request.trustedHelper
      )
    )
  }

  def newWrapperLayout: Action[AnyContent] = authenticate { implicit request =>
    Ok(
      wrapperService.standardScaLayout(
        content = view(""),
        serviceURLs = ServiceURLs(signOutUrl = Some("/logout")),
        pageTitle = Some(Messages("page.title")),
        hideMenuBar = false,
        optTrustedHelper = request.trustedHelper
      )
    )
  }

}
