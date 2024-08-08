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
import config.FrontendAppConfig
import models.carbonintensity.CarbonIntensityDetails
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CarbonIntensityDataConnector @Inject() (
  val httpClientV2: HttpClientV2,
  val frontendAppConfig: FrontendAppConfig,
  val httpClientResponse: HttpClientResponse
) {

  private lazy val carbonIntensityBaseUrl: String  = frontendAppConfig.carbonIntensityBaseUrl
  private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'")

  def getCarbonIntensityData(
    fromDate: LocalDateTime,
    toDate: LocalDateTime,
    postcode: String
  )(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): EitherT[Future, UpstreamErrorResponse, Seq[CarbonIntensityDetails]] = {

    val from: String = fromDate.format(dateTimeFormatter)
    val to: String   = toDate.format(dateTimeFormatter)

    //TODO: HANDLE FULLPOST CODE - API returns 200 with null when full postcode is passed. use regex ?
    val outwardPostcode: String = postcode.split(" ").head

    val url: String = s"$carbonIntensityBaseUrl/regional/intensity/$from/$to/postcode/$outwardPostcode"

    println("\n \n URL : " + url)

    val apiResponse: Future[Either[UpstreamErrorResponse, HttpResponse]] = httpClientV2
      .get(url"$url")
      .execute[Either[UpstreamErrorResponse, HttpResponse]](readEitherOf(readRaw), ec)

    httpClientResponse
      .read(apiResponse)
      .map { response =>
        (response.json \ "data").as[Seq[CarbonIntensityDetails]]
      }
  }
}
