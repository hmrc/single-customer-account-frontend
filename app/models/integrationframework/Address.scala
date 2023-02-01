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

package models.integrationframework

import play.api.libs.json.{Json, OFormat}

case class Address( addressLine1: Option[String] = None,
                    addressLine2: Option[String] = None,
                    addressLine3: Option[String] = None,
                    addressLine4: Option[String] = None,
                    addressLine5: Option[String] = None,
                    addressPostcode: Option[String] = None,
                    countryCode: Option[Int] = None )

object Address {

  implicit val format: OFormat[Address] = Json.format[Address]

  def apply(address: IfAddress): Address = new Address(
    addressLine1 = address.addressLine1,
    addressLine2 = address.addressLine2,
    addressLine3 = address.addressLine3,
    addressLine4 = address.addressLine4,
    addressLine5 = address.addressLine5,
    addressPostcode = address.addressPostcode,
    countryCode = address.countryCode
  )

}
