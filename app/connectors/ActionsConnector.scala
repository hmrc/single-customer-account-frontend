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
import models.integrationframework.Actions
import play.api.Logging
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderNames

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ActionsConnector @Inject()(
                                  val wsClient: WSClient,
                                  appConfig: FrontendAppConfig
                                )(implicit executionContext: ExecutionContext) extends Logging {

  private def setHeaders = Seq(
    (HeaderNames.authorisation, s"Bearer ${appConfig.integrationFrameworkAuthToken}"),
    "Environment" -> appConfig.integrationFrameworkEnvironment,
    "CorrelationId" -> UUID.randomUUID().toString
  )

  def getActions(nino: Nino): Future[Actions] = {

    wsClient.url(s"${appConfig.capabilitiesDataBaseUrl}/single-customer-account-capabilities/actions/${nino.nino}")
      .withHttpHeaders(setHeaders: _*)
      .get().map {
      case response if response.status >= OK && response.status < 300 =>
        logger.info(s"[ActionsConnector][getActions] IF successful response code: ${response.status}")
        response.json.as[Actions]
      case response if response.status == NOT_FOUND =>
        logger.info("[ActionsConnector][getActions] IF returned code 404 NOT FOUND")
        Actions(Seq.empty)
      case response =>
        logger.warn(s"[ActionsConnector][getActions] IF returned unknown code: ${response.status}")
        Actions(Seq.empty)
    }.recover {
      case ex: Exception =>
        logger.error(s"[ActionsConnector][getActions] exception: ${ex.getMessage}")
        Actions(Seq.empty)
    }

  }

}
