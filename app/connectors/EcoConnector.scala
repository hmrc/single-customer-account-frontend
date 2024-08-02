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

import models.auth.EcoConnectorModel
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject

//api.carbonintensity.org.uk/regional/intensity/{from}/{to}/postcode/{postcode}

class EcoConnector @Inject() (httpClientV2: HttpClientV2) {
//  ISO8601 format YYYY-MM-DDThh:mmZ e.g. 2017-08-25T12:35Z
  def get(start: String, end: String, postcode: String)(implicit hc: HeaderCarrier): EcoConnectorModel = {
    httpClientV2.get(
      url"api.carbonintensity.org.uk/regional/intensity/2017-08-25T12:35Z/2017-08-25T12:35Z/postcode/NE34PL"
    )
    EcoConnectorModel(Nil, "moderate")
  }
}
