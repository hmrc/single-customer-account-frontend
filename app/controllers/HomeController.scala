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

import config.FrontendAppConfig
import controllers.actions.{AuthAction, IFAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.sca.controllers.TestLibrary
import views.html.HomeView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class HomeController @Inject()(
                                val controllerComponents: MessagesControllerComponents,
                                authenticate: AuthAction,
                                getUserDetails: IFAction,
                                view: HomeView,
                                testLibrary: TestLibrary
                              )(implicit frontendAppConfig: FrontendAppConfig, executionContext: ExecutionContext) extends FrontendBaseController with I18nSupport {



  def onPageLoad: Action[AnyContent] = (authenticate andThen getUserDetails) { implicit request =>

    val name = request.ifData.details.name.fold("") { name => s"${name.firstForename.getOrElse("")} ${name.surname.getOrElse("")}" }
    Ok(view(name))
  }
}
