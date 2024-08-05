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
    val apiUrl                  = s"${appConfig.ecoBaseUrl}/regional/intensity/$start/$end/postcode/$postcode"
    println("\n ****** " + apiUrl)
    val x: Future[HttpResponse] = httpClientV2
      .get(
        url"$apiUrl"
      )
      .execute
    x.map { r =>
      val a = r.json \ "data"
      println("\n ***** JSON: " + a)

      (r.json \ "data").as[EcoConnectorModel]

      // ([{"regionid":3,"dnoregion":"Electricity North West","shortname":"North West England","postcode":"RG10","data":[{"from":"2018-01-20T12:00Z","to":"2018-01-20T12:30Z","intensity":{"forecast":266,"index":"moderate"},"generationmix":[{"fuel":"gas","perc":43.6},{"fuel":"coal","perc":0.7}]}]}])

    }

  }
}
