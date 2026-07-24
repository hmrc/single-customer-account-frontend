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

import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.hmrcfrontend.config.AccessibilityStatementConfig
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.hmrcstandardpage.ServiceURLs
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.sca.services.WrapperService
import uk.gov.hmrc.sca.utils.Keys.getTrustedHelperFromRequest
import views.html.HomeViewWrapperVersion

import javax.inject.Inject

class HomeController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  view: HomeViewWrapperVersion,
  wrapperService: WrapperService,
  accessibilityStatementConfig: AccessibilityStatementConfig
) extends FrontendBaseController
    with I18nSupport {

  def newWrapperLayout: Action[AnyContent] = Action { implicit request =>
    Ok(
      wrapperService.standardScaLayout(
        content = view(""),
        serviceURLs = ServiceURLs(
          signOutUrl = Some("/logout"),
          accessibilityStatementUrl = accessibilityStatementConfig.url
        ),
        pageTitle = Some(Messages("page.title")),
        hideMenuBar = false,
        optTrustedHelper = getTrustedHelperFromRequest(request)
      )
    )
  }

}
