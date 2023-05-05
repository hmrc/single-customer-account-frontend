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

import play.api.libs.json.{Format, Json, Reads}
import uk.gov.hmrc.auth.core.Nino

import java.time.LocalDate

case class IfDesignatoryDetails( details: IfDetails,
                                 nameList: IfNameList,
                                 addressList: IfAddressList )

object IfDesignatoryDetails {

  implicit val reads: Reads[IfDesignatoryDetails] = Json.reads[IfDesignatoryDetails]

}

case class IfCapabilityDetails(
                                nino: Nino,
                                date: String,
                                descriptionContent: String,
                                url: String
                              )

object IfCapabilityDetails {

  implicit val format: Format[IfCapabilityDetails] = Json.format[IfCapabilityDetails]

}

case class IfDetails(marriageStatusType: Option[Int] = None)

object IfDetails {

  implicit val reads: Reads[IfDetails] = Json.reads[IfDetails]

}

case class IfNameList(name: Seq[IfName])

object IfNameList {

  implicit val reads: Reads[IfNameList] = Json.reads[IfNameList]

}

case class IfName(nameSequenceNumber: Option[Int] = None,
                  nameType: Option[Int] = None,
                  titleType: Option[Int] = None,
                  requestedName: Option[String] = None,
                  nameStartDate: Option[LocalDate] = None,
                  nameEndDate: Option[LocalDate] = None,
                  firstForename: Option[String] = None,
                  secondForename: Option[String] = None,
                  surname: Option[String] = None)

object IfName {

  implicit val reads: Reads[IfName] = Json.reads[IfName]

}

case class IfAddressList(address: Seq[IfAddress])

object IfAddressList {

  implicit val reads: Reads[IfAddressList] = Json.reads[IfAddressList]

}

case class IfAddress( addressSequenceNumber: Option[Int] = None,
                      countryCode: Option[Int] = None,
                      addressType: Option[Int] = None,
                      addressStartDate: Option[LocalDate] = None,
                      addressEndDate: Option[LocalDate] = None,
                      addressLine1: Option[String] = None,
                      addressLine2: Option[String] = None,
                      addressLine3: Option[String] = None,
                      addressLine4: Option[String] = None,
                      addressLine5: Option[String] = None,
                      addressPostcode: Option[String] = None)

object IfAddress {

  implicit val reads: Reads[IfAddress] = Json.reads[IfAddress]

}
