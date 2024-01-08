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

package connectors

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import models.integrationframework.{IFContactDetails, IfDesignatoryDetails}
import play.api.Logging
import play.api.http.Status._
import play.api.libs.ws.WSClient
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderNames

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IFConnector @Inject()(
                             val wsClient: WSClient,
                             appConfig: FrontendAppConfig)
                           (implicit executionContext: ExecutionContext) extends Logging {

  private val designatoryDetailsFields: String =
    "details(marriageStatusType),nameList(name(nameSequenceNumber,nameType,titleType," +
      "requestedName,nameStartDate,nameEndDate,firstForename,secondForename,surname))," +
      "addressList(address(addressSequenceNumber,countryCode,addressType,addressStartDate," +
      "addressEndDate,addressLine1,addressLine2,addressLine3,addressLine4,addressLine5," +
      "addressPostcode))"

  private val contactDetailsFields: String = "contactDetails(code,type,detail)"

  private def setHeaders = Seq(
    (HeaderNames.authorisation, s"Bearer ${appConfig.integrationFrameworkAuthToken}"),
    ("Environment" -> appConfig.integrationFrameworkEnvironment),
    ("CorrelationId" -> UUID.randomUUID().toString)
  )

  def getDesignatoryDetails(nino: Option[Nino]): Future[Option[IfDesignatoryDetails]] = {
    nino match {
      case None => logger.info("[IFConnector][getDesignatoryDetails] No NINO supplied, defaulting to None")
        Future.successful(None)
      case Some(ninoString) => {
        val headers = setHeaders ++ Seq(("OriginatorId", "DA2_BS_UNATTENDED"))
        wsClient.url(s"${appConfig.integrationFrameworkUrl}/individuals/details/NINO/${ninoString.nino}?fields=$designatoryDetailsFields")
          .withHttpHeaders(headers: _*)
          .get().map {
            case response if response.status >= OK && response.status < 300 =>
              logger.info(s"[IFConnector][getDesignatoryDetails] IF successful response code: ${response.status}")
              response.json.asOpt[IfDesignatoryDetails]
            case response if response.status == LOCKED =>
              logger.info("[IFConnector][getDesignatoryDetails] IF returned code 423 LOCKED, personal details record hidden")
              None
            case response if response.status == NOT_FOUND =>
              logger.info("[IFConnector][getDesignatoryDetails] IF returned code 404 NOT FOUND")
              None
            case response => logger.warn(s"[IFConnector][getDesignatoryDetails] IF returned unknown code: ${response.status}")
              None
        }.recover {
          case ex: Exception =>
            logger.error(s"[IFConnector][getDesignatoryDetails] exception: ${ex.getMessage}")
            None
        }
      }
    }
  }

  def getContactDetails(nino: Option[Nino]): Future[Option[IFContactDetails]] = {
    nino match {
      case None => logger.info("[IFConnector][getContactDetails] No NINO supplied, defaulting to None")
        Future.successful(None)
      case Some(ninoString) => {
        wsClient.url(s"${appConfig.integrationFrameworkUrl}/individuals/details/contact/nino/${ninoString.nino}?fields=$contactDetailsFields")
          .withHttpHeaders(setHeaders: _*)
          .get().map {
          case response if response.status >= OK && response.status < 300 =>
            logger.info(s"[IFConnector][getContactDetails] IF successful response code: ${response.status}")
            response.json.asOpt[IFContactDetails]
          case response if response.status == NOT_FOUND =>
            logger.info("[IFConnector][getContactDetails] IF returned code 404 NOT FOUND")
            None
          case response =>
            logger.warn(s"[IFConnector][getContactDetails] IF returned unknown code: ${response.status}")
            None
        }.recover {
          case ex: Exception =>
            logger.error(s"[IFConnector][getContactDetails] exception: ${ex.getMessage}")
            None
        }
      }
    }
  }

}
