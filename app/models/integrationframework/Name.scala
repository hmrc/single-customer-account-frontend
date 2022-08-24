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

import play.api.libs.json.{Json, OFormat}

case class Name( firstForename: Option[String] = None,
                 secondForename: Option[String] = None,
                 surname: Option[String] = None,
                 title: Option[Int] = None)

object Name {

  implicit val format: OFormat[Name] = Json.format[Name]

  def apply(name: IfName): Name = new Name(
    firstForename = name.firstForename,
    secondForename = name.secondForename,
    surname = name.surname,
    title = name.titleType
  )
}
