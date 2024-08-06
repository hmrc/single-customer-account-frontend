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

package models.carbonintensity

import play.api.libs.json.{Format, Json}

case class CarbonIntensityDetails(
  regionid: Int,
  dnoregion: String,
  shortname: String,
  postcode: String,
  data: Seq[CarbonIntensityData]
)

object CarbonIntensityDetails {
  implicit val formats: Format[CarbonIntensityDetails] = Json.format[CarbonIntensityDetails]
}
