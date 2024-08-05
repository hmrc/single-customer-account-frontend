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

import cats.data.EitherT
import com.google.inject.Inject
import config.FrontendAppConfig
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

class CarbonIntensityDataConnector @Inject() (
  val httpClientV2: HttpClientV2,
  val frontendAppConfig: FrontendAppConfig,
  val httpClientResponse: HttpClientResponse
) {

  private lazy val carbonIntensityBaseUrl: String = frontendAppConfig.carbonIntensityBaseUrl

  def getCarbonIntensityData(
    fromDate: String,
    toDate: String,
    postcode: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): EitherT[Future, UpstreamErrorResponse, HttpResponse] = {

    val url                                                              = s"$carbonIntensityBaseUrl/regional/intensity/$fromDate/$toDate/postcode/$postcode"
    val apiResponse: Future[Either[UpstreamErrorResponse, HttpResponse]] = httpClientV2
      .get(url"$url")
      .execute[Either[UpstreamErrorResponse, HttpResponse]](readEitherOf(readRaw), ec)

    httpClientResponse.read(apiResponse)
  }
}
