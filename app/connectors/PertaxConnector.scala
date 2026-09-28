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

package connectors

import config.FrontendAppConfig
import play.api.Logging
import play.api.http.HeaderNames
import play.api.mvc.RequestHeader

import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.partials.{HeaderCarrierForPartialsConverter, HtmlPartial}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class PertaxConnector @Inject() (
  httpClientV2: HttpClientV2,
  frontendAppConfig: FrontendAppConfig,
  headerCarrierForPartialsConverter: HeaderCarrierForPartialsConverter
) extends Logging {

  def authorise()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url = s"${frontendAppConfig.pertaxUrl}/pertax/authorise"

    httpClientV2
      .post(url"$url")
      .setHeader(HeaderNames.ACCEPT -> "application/vnd.hmrc.2.0+json")
      .execute[HttpResponse]
  }

  def loadPartial(partialPath: String)(implicit request: RequestHeader, ec: ExecutionContext): Future[HtmlPartial] = {
    implicit val hc: HeaderCarrier =
      headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(request)
    val partialUrl                 = s"${frontendAppConfig.pertaxUrl}$partialPath"

    httpClientV2
      .get(url"$partialUrl")
      .execute[HtmlPartial]
      .map {
        case partial: HtmlPartial.Success =>
          partial
        case partial: HtmlPartial.Failure =>
          logger.error(s"Failed to load partial from $partialPath, partial info: $partial, body: ${partial.body}")
          partial
      }
      .recover { case NonFatal(e) =>
        logger.error(s"Failed to load partial from $partialPath", e)
        e match {
          case ex: HttpException => HtmlPartial.Failure(Some(ex.responseCode))
          case _                 => HtmlPartial.Failure(None)
        }
      }
  }
}
