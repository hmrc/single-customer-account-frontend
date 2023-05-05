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



import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.{AuthAction, CapabilityAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CapabilityDetailsView
import scala.concurrent.ExecutionContext

class CapabilityDetailsController @Inject()(
                                             val controllerComponents: MessagesControllerComponents,
                                             authenticate: AuthAction,
                                             getCapabilityDetails: CapabilityAction,
                                             config: FrontendAppConfig,
                                             view: CapabilityDetailsView
                                           )(implicit frontendAppConfig: FrontendAppConfig, executionContext: ExecutionContext) extends FrontendBaseController with I18nSupport  {

  def getCapabilitiesData: Action[AnyContent] = (authenticate andThen getCapabilityDetails) { implicit request =>

    val date = request.ifCapabilitiesData.date
    val desc = request.ifCapabilitiesData.descriptionContent
    val url = request.ifCapabilitiesData.url

    Ok(view(date, desc, url))

  }


}
