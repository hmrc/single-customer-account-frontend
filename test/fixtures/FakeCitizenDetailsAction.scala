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

package fixtures

import config.FrontendAppConfig
import connectors.CitizenDetailsConnector
import controllers.actions.CitizenDetailsActionImpl
import models.auth.{AuthenticatedDetailsRequest, AuthenticatedRequest}
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FakeCitizenDetailsAction @Inject()(override val citizenDetailsConnector: CitizenDetailsConnector,
                                         config: FrontendAppConfig,
                                         override val parser: BodyParsers.Default)
                                        (implicit ec: ExecutionContext) extends CitizenDetailsActionImpl(citizenDetailsConnector, config, parser) {

  override protected def transform[A](request: AuthenticatedRequest[A]): Future[AuthenticatedDetailsRequest[A]] = {
    Future.successful(TestData.Requests.authenticatedDetailsRequest(request))
  }
}

