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

import controllers.actions.IFActionImpl
import models.auth.{AuthenticatedIFRequest, AuthenticatedRequest}
import play.api.mvc._
import services.IFService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FakeIFAction @Inject()(override val ifService: IFService,
                             override val parser: BodyParsers.Default)
                            (implicit ec: ExecutionContext) extends IFActionImpl(ifService, parser) {

  override protected def transform[A](request: AuthenticatedRequest[A]): Future[AuthenticatedIFRequest[A]] = {
    Future.successful(TestData.Requests.authenticatedDetailsRequest(request))
  }
}
