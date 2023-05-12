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
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CapabilityService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CapabilityDetailsView

import scala.concurrent.ExecutionContext

class CapabilityDetailsController @Inject()(
                                             val controllerComponents: MessagesControllerComponents,
                                             val capabilityService: CapabilityService,
                                             config: FrontendAppConfig,
                                             view: CapabilityDetailsView
                                           )(implicit frontendAppConfig: FrontendAppConfig, executionContext: ExecutionContext) extends FrontendBaseController with I18nSupport  {

  def getCapabilitiesData: Action[AnyContent] = Action.async {implicit request =>
  val ifCapabilityDetails = capabilityService.getCapabilityDetails(Some(Nino("GG012345C")))
    println(s"Thisis the capDetails: $ifCapabilityDetails")
    ifCapabilityDetails.map { capabilityDetails =>
      val date = capabilityDetails.date
      val desc = capabilityDetails.descriptionContent
      val url = capabilityDetails.url
      println(date)

      Ok(view(date,desc,url))
    }
  }
}
