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

package connectors

import com.google.inject.{Inject, Singleton}
import com.kenshoo.play.metrics.Metrics
import models.response.{MatchingDetails, PersonDetails}
import play.api.Logging
import play.api.http.Status._
import play.api.libs.ws.WSClient
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CitizenDetailsConnector @Inject()(
                                         val wsClient: WSClient,
                                         val metrics: Metrics,
                                         servicesConfig: ServicesConfig)
                                       (implicit executionContext: ExecutionContext) extends Logging {

  lazy val citizenDetailsUrl: String = servicesConfig.baseUrl("citizen-details")

  def personDetails(nino: Nino)(implicit hc: HeaderCarrier): Future[Either[String, PersonDetails]] = {
    wsClient.url(s"$citizenDetailsUrl/citizen-details/$nino/designatory-details").get().map {
      case response if response.status >= OK && response.status < 300 => Right(response.json.as[PersonDetails])
      case response if response.status== LOCKED => Left("") //personal details record hidden
      case response if response.status == NOT_FOUND => Left("")
      case response => Left("")
    }.recover {
      case ex: Exception => Left("")
    }
  }

  def getMatchingDetails(nino: Nino)(implicit hc: HeaderCarrier): Future[Either[String,MatchingDetails]] = {
    wsClient.url(s"$citizenDetailsUrl/citizen-details/nino/$nino").get().map {
      case response if response.status >= OK && response.status < 300 => Right(MatchingDetails.fromJsonMatchingDetails(response.json))
      case response if response.status == NOT_FOUND => Left("")
      case response => Left("")
    }.recover {
      case ex: Exception => Left("")
    }
  }

}
