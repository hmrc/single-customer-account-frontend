/*
 * Copyright 2024 HM Revenue & Customs
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
import models.auth.EcoConnectorModel
import uk.gov.hmrc.http.HttpReads.Implicits.{readEitherOf, readRaw}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EcoConnector @Inject() (httpClientV2: HttpClientV2, appConfig: FrontendAppConfig) {
  def get(start: LocalDateTime, end: LocalDateTime, postcode: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[UpstreamErrorResponse, Seq[EcoConnectorModel]]] = {

    val formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    val startAsString = start.format(formatter) + "Z"
    val endAsString   = end.format(formatter) + "Z"

    val apiUrl                                                 = s"${appConfig.ecoBaseUrl}/regional/intensity/$startAsString/$endAsString/postcode/$postcode"
    println("\n ****** " + apiUrl)
    val futureEither: Future[Either[UpstreamErrorResponse, HttpResponse]] = httpClientV2
      .get(
        url"$apiUrl"
      )
      .execute[Either[UpstreamErrorResponse, HttpResponse]](readEitherOf(readRaw), ec)

    futureEither.map {
      case ri @ Right(_)       =>
        ri.map { r =>
          (r.json \ "data").as[Seq[EcoConnectorModel]]
        }
      case Left(errorResponse) =>
        val either: Either[UpstreamErrorResponse, Seq[EcoConnectorModel]] = Left(errorResponse)
        either
    }

  }
}
