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

package controllers.actions

import com.google.inject.{ImplementedBy, Inject}
import models.auth.{AuthenticatedIFRequest, AuthenticatedRequest}
import play.api.Logging
import play.api.mvc._
import services.IFService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class IFActionImpl @Inject()(
                              val ifService: IFService,
                              val parser: BodyParsers.Default)
                            (implicit val executionContext: ExecutionContext) extends IFAction with Logging {

  override protected def transform[A](request: AuthenticatedRequest[A]): Future[AuthenticatedIFRequest[A]] = {
    ifService.getPersonalDetails(request.nino).map { res =>
      logger.info(s"[IFActionImpl][transform] Successful IF Action request")
      AuthenticatedIFRequest[A](request, res)
    }.recoverWith {
      case ex: Exception =>
        logger.error(s"[IFActionImpl][transform] exception: ${ex.getMessage}")
        Future.failed(ex)
    }
  }
}

@ImplementedBy(classOf[IFActionImpl])
trait IFAction extends ActionTransformer[AuthenticatedRequest, AuthenticatedIFRequest]
