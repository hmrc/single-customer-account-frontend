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
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future

//api.carbonintensity.org.uk/regional/intensity/{from}/{to}/postcode/{postcode}

class EcoConnector @Inject() (httpClientV2: HttpClientV2, appConfig: FrontendAppConfig) {
  //  ISO8601 format YYYY-MM-DDThh:mmZ e.g. 2017-08-25T12:35
  def get(start: String, end: String, postcode: String)(implicit hc: HeaderCarrier): Future[EcoConnectorModel] = {
    val apiUrl                  = s"${appConfig.ecoBaseUrl}/regional/intensity/2017-08-25T12:35Z/2017-08-25T12:35Z/postcode/NE34PL"
    println("\n ****** " + apiUrl)
    val x: Future[HttpResponse] = httpClientV2
      .get(
        url"$apiUrl"
      )
      .execute
    x.map { r =>
      println("\n ***** JSON: " + r.json)
      EcoConnectorModel(Nil, "moderate")
    }

  }
}
