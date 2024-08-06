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

package models.auth

import play.api.libs.json._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import scala.util.{Failure, Success, Try}

/*

"data":[
  {
    "regionid": 3,
    "dnoregion": "Electricity North West",
    "shortname": "North West England",
    "postcode": "RG10",
    "data":[
    {
      "from": "2018-01-20T12:00Z",
      "to": "2018-01-20T12:30Z",
      "intensity": {
        "forecast": 266,
        "index": "moderate"
      },
      "generationmix": [
      {
        "fuel": "gas",
        "perc": 43.6
      },
      {
        "fuel": "coal",
        "perc": 0.7
      }
      ]
    }]
  }]

 */

case class Intensity(
  forecast: Int,
  index: String
)

object Intensity {
  implicit val formats: Format[Intensity] = Json.format[Intensity]
}

case class GenerationMix(
  fuel: String,
  perc: BigDecimal
)

object GenerationMix {
  implicit val formats: Format[GenerationMix] = Json.format[GenerationMix]
}

case class EcoConnectorModel(
  regionid: Int,
  dnoregion: String,
  shortname: String,
  postcode: String,
  data: Seq[Data]
)

object EcoConnectorModel {
  implicit val formats: Format[EcoConnectorModel] = Json.format[EcoConnectorModel]
}

case class Data(
  from: LocalDateTime,
  to: LocalDateTime,
  intensity: Intensity,
  generationmix: Seq[GenerationMix]
)

object Data {
  implicit val localDateTimeRead: Reads[LocalDateTime] = {
    case JsString(s) =>
      Try(LocalDateTime.parse(s, ISO_DATE_TIME)) match {
        case Failure(e) =>
          JsError(
            s"Could not parse $s as a LocalDateTime : ${e.getMessage}"
          )
        case Success(v) => JsSuccess(v)
      }
    case json        => JsError(s"Expected value to be a string, was actually ${json.toString}")
  }

  implicit val formats: Format[Data] = Json.format[Data]
}
