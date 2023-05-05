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
import models.integrationframework.IfCapabilityDetails
import play.api.Logging
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderNames

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class CapabilityConnector @Inject()(
                                   val wsClient: WSClient,
                                   appConfig: FrontendAppConfig
                                   ) (implicit executionContext: ExecutionContext) extends Logging {

  private val capabilityDetailsFields: String = "capabilityDetails(nino,date,descriptionContent,url)"

  private def setHeaders = Seq(
    (HeaderNames.authorisation, s"Bearer ${appConfig.integrationFrameworkAuthToken}"),
    ("Environment" -> appConfig.integrationFrameworkEnvironment),
    ("CorrelationId" -> UUID.randomUUID().toString)
  )

  def getCapabilityDetails(nino: Option[Nino]): Future[Option[IfCapabilityDetails]] = {
    nino match {
      case None => logger.info("[CapabilityConnector][getCapabilityDetails] No NINO supplied, defaulting to None")
        Future.successful(None)
      case Some(ninoString) => {
        wsClient.url(s"${appConfig.capabilitiesDataBaseUrl}/single-customer-account-stub/individuals/details/NINO/${ninoString.nino}?fields=$capabilityDetailsFields")
          .withHttpHeaders(setHeaders: _*)
          .get().map {
          case response if response.status >= OK && response.status < 300 =>
            logger.info(s"[CapabilityConnector][getCapabilityDetails] IF successful response code: ${response.status}")
            response.json.asOpt[IfCapabilityDetails]
          case response if response.status == NOT_FOUND =>
            logger.info("[CapabilityConnector][getCapabilityDetails] IF returned code 404 NOT FOUND")
            None
          case response =>
            logger.warn(s"[CapabilityConnector][getCapabilityDetails] IF returned unknown code: ${response.status}")
            None
        }.recover {
          case ex: Exception =>
            logger.error(s"[CapabilityConnector][getCapabilityDetails] exception: ${ex.getMessage}")
            None
        }
      }
    }
  }




}
