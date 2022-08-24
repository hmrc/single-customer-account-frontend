/*
 * Copyright 2022 HM Revenue & Customs
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

package models.integrationframework

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

case class IFContactDetail(code: Int, contactType: String, detail: String)

object IFContactDetail {

  implicit val format: Format[IFContactDetail] = Format(
    (
      (JsPath \ "code").read[Int] and
        (JsPath \ "type").read[String] and
        (JsPath \ "detail").read[String]
      )(IFContactDetail.apply _),
    (
      (JsPath \ "code").write[Int] and
        (JsPath \ "type").write[String] and
        (JsPath \ "detail").write[String]
      )(unlift(IFContactDetail.unapply))
  )
}
