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
import models.auth.{AuthenticatedCapabilityRequest, AuthenticatedRequest}
import play.api.Logging
import play.api.mvc.{ActionTransformer, BodyParsers}
import services.{CapabilityService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class CapabilityActionImpl @Inject()(
                              val capabilityService: CapabilityService,
                              val parser: BodyParsers.Default)
                            (implicit val executionContext: ExecutionContext) extends CapabilityAction with Logging {

  override protected def transform[A](request: AuthenticatedRequest[A]): Future[AuthenticatedCapabilityRequest[A]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    capabilityService.getCapabilityDetails(request.nino).map { res =>
      logger.info(s"[CapabilityActionImpl][transform] Successful CapabilityAction request")
      AuthenticatedCapabilityRequest[A](request, res)
    }.recoverWith {
      case ex: Exception =>
        logger.error(s"[CapabilityActionImpl][transform] exception: ${ex.getMessage}")
        Future.failed(ex)
    }
  }
}

@ImplementedBy(classOf[CapabilityActionImpl])
trait CapabilityAction extends ActionTransformer[AuthenticatedRequest, AuthenticatedCapabilityRequest]
