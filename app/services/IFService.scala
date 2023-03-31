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

package services

import connectors.IFConnector
import models.integrationframework._
import play.api.Logging
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IFService @Inject()(connector: IFConnector)(implicit ec: ExecutionContext) extends Logging {

  // TODO: Check assumption behind name logic
  private def getName(designatoryDetails: IfDesignatoryDetails): Option[IfName] = {
    val realKnownAsNames: (Seq[IfName], Seq[IfName]) = designatoryDetails.nameList.name.partition(_.nameType.contains(1))
    val name: Option[IfName] = if (realKnownAsNames._1.nonEmpty) {
      realKnownAsNames._1.sortBy(_.nameSequenceNumber).takeRight(1).headOption
    } else {
      realKnownAsNames._2.sortBy(_.nameSequenceNumber).takeRight(1).headOption
    }
    name
  }

  // TODO: Check assumption that address / nameIncrementNumber can be used to find most recent address / name

  private def getAddresses(designatoryDetails: IfDesignatoryDetails): (Option[Address], Option[Address]) = {
    val residentialCorrespondenceAddresses: (Seq[IfAddress], Seq[IfAddress]) = designatoryDetails.addressList.address.partition(_.addressType.contains(1))

    val residentialAddress = residentialCorrespondenceAddresses._1.sortBy(_.addressSequenceNumber).takeRight(1).headOption.map(Address.apply)
    val correspondenceAddress = residentialCorrespondenceAddresses._2.sortBy(_.addressSequenceNumber).takeRight(1).headOption.map(Address.apply)

    (residentialAddress, correspondenceAddress)
  }

  // TODO: Check assumption of email and phone number orderings

  private def getContactDetails(contactDetails: IFContactDetails): Option[ContactDetails] = {
    val emailAddress: Option[String] = contactDetails.contactDetails.flatMap(
      _.filter(_.contactType.contains("E-MAIL")).sortBy(_.code).headOption).map(_.detail)
    val phoneNumber: Option[String] = contactDetails.contactDetails.flatMap(
      _.filter(_.contactType.contains("TELEPHONE")).sortBy(_.code).headOption).map(_.detail)

    (emailAddress, phoneNumber) match {
      case (None, None) => None
      case _ => Some(ContactDetails(emailAddress, phoneNumber))
    }
  }

  def getPersonalDetails(nino: Option[Nino])(implicit hc: HeaderCarrier): Future[PersonalDetailsResponse] = {
    for {
      designatoryDetails <- connector.getDesignatoryDetails(nino)
      contactDetails <- connector.getContactDetails(nino)
    } yield {
      (designatoryDetails, contactDetails) match {
        case (Some(dDetails), Some(cDetails)) =>
          val addresses = getAddresses(dDetails)
          val name = getName(dDetails)

          PersonalDetailsResponse(
            details = PersonalDetails(
              name = name.map(Name.apply),
              requestedName = name.flatMap(_.requestedName),
              maritalStatus = designatoryDetails.get.details.marriageStatusType),
            contactDetails = getContactDetails(cDetails),
            residentialAddress = addresses._1,
            correspondenceAddress = addresses._2
          )
        case _ => PersonalDetailsResponse()
      }
    }
  }
}
