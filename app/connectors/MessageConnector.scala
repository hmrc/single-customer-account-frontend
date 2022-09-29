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

package connectors

import com.google.inject.{Inject, Singleton}
import com.kenshoo.play.metrics.Metrics
import config.FrontendAppConfig
import models.integrationframework.{IFContactDetails, IfDesignatoryDetails}
import play.api.Logging
import play.api.http.Status._
import play.api.libs.ws.WSClient
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpClient, HttpException}
import uk.gov.hmrc.play.partials.{HeaderCarrierForPartialsConverter, HtmlPartial}
import uk.gov.hmrc.play.partials.HtmlPartial._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MessageConnector @Inject()(
                                  http: HttpClient,
                                  val metrics: Metrics,
                                  headerCarrierForPartialsConverter: HeaderCarrierForPartialsConverter,
                                  appConfig: FrontendAppConfig)
                                (implicit executionContext: ExecutionContext) extends Logging {

  def getMessages(requestHeader: RequestHeader): Future[HtmlPartial] = {
    implicit val hc: HeaderCarrier = headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(requestHeader)

    http.GET[HtmlPartial]("http://localhost:9060/messages/VEEvZHR4cE85S3BWcUx0Z0ErNnFReHFXbS9uNlIzWGJLMC9BTHo3YWVySU03d3lLeGNxbUxNQmFwdXFMUEJ2ODNNRmpwZ3BoZy9qZHRuSkNSNHBiYjI5R1RHMVAxeDJwOTlXSWFEZGNhQXlWSDk0MU9HWlJXVy9vdWZNSVlyR1lQRWNrTEVXSEZjTS9FVktIVUhsa2VBNHI2a1k2bHRIeW1kZTZtYy9waEs4bXF4TzhZYkUvdWcrNFUxSDRjeElKYjFVRFFGVG5mczN5eVJaakw0T0FRWkI0a0Y4SlRrQ0RSN0lKNUpPdVNqQjUwbUQ5dnQ0elBpQjQxNXNQc0JvZHNicEhPRk9lbUdRaUhEMjFRczYvQm5YUG9OUmQydzJ1MC9yTDVWS250M1hxZHVPZGxuaUcrWmRRbjMwT3JQcjhvM2ZKZWtTcjhoVlNDTDZFMTF1ZlhPUXFjNEh1OGZrWExSMTJYcDBUdGxVdTdlY3BtM1hXUGY2QThML1djSjhhd1E5dVFRcVhJKy81dXVqTWxJTndLYTh2YVZhWkxkdWp3Rk1Zb0taZjJuRnROS2hSTjFJTWFucm85S1hJanFtMFZZQnh5dUZXUUJRWkFHcGU1dFNnbXc9PQ==") map {
      case partial: HtmlPartial.Success => partial
      case partial: HtmlPartial.Failure =>
        logger.error(s"Failed to load partial, partial info: $partial")
        partial
    } recover { case e =>
      logger.error(s"Failed to load partial", e)
      e match {
        case ex: HttpException => HtmlPartial.Failure(Some(ex.responseCode))
        case _ => HtmlPartial.Failure(None)
      }
    }
  }



}
