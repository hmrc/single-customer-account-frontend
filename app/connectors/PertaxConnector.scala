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
import play.api.http.HeaderNames

import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PertaxConnector @Inject() (
  httpClientV2: HttpClientV2,
  frontendAppConfig: FrontendAppConfig
) {
  
  def authorise()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url = s"${frontendAppConfig.pertaxUrl}/pertax/authorise"

    httpClientV2
      .post(url"$url")
      .setHeader(HeaderNames.ACCEPT -> "application/vnd.hmrc.2.0+json")
      .execute[HttpResponse]
  }
}
