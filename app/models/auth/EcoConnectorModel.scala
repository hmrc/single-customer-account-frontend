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

import play.api.libs.json.{Format, Json}

/*
{
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
      }
      "generationmix": [
      {
        "fuel": "gas",
        "perc": 43.6
      },
      {
        "fuel": "coal",
        "perc": 0.7
      },
      {
        "fuel": "biomass",
        "perc": 4.2
      },
      {
        "fuel": "nuclear",
        "perc": 17.6
      },
      {
        "fuel": "hydro",
        "perc": 2.2
      },
      {
        "fuel": "imports",
        "perc": 6.5
      },
      {
        "fuel": "other",
        "perc": 0.3
      },
      {
        "fuel": "wind",
        "perc": 6.8
      },
      {
        "fuel": "solar",
        "perc": 18.1
      }
      ]
    }]
  }]
}
 */

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
  from: String,
  to: String,
  intensity: Intensity,
  generationmix: Seq[GenerationMix]
)

object Data {
  implicit val formats: Format[Data] = Json.format[Data]
}
