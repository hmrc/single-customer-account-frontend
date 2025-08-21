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

package fixtures

import config.FrontendAppConfig
import controllers.actions.AuthActionImpl
import models.auth.AuthenticatedRequest
import play.api.mvc.*
import uk.gov.hmrc.auth.core.AuthConnector

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FakeAuthAction @Inject() (
  override val authConnector: AuthConnector,
  config: FrontendAppConfig,
  override val parser: BodyParsers.Default
)(implicit ec: ExecutionContext)
    extends AuthActionImpl(authConnector, config, parser) {

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    block(TestData.Requests.authenticatedRequest(request))
}
