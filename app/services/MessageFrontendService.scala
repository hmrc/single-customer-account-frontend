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

package services

import com.codahale.metrics.Timer
import config.FrontendAppConfig
import metrics.{Metrics, MetricsEnumeration}
import play.api.Logging
import models.MessageCount
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.play.partials.{HeaderCarrierForPartialsConverter, HtmlPartial}
import utils.EnhancedPartialRetriever

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MessageFrontendService @Inject() (
                                         http: HttpClient,
                                         metrics: Metrics,
                                         headerCarrierForPartialsConverter: HeaderCarrierForPartialsConverter,
                                         servicesConfig: FrontendAppConfig,
                                         enhancedPartialRetriever: EnhancedPartialRetriever
                                       )(implicit executionContext: ExecutionContext)
  extends Logging {

  lazy val messageFrontendUrl: String = servicesConfig.messageFrontendUrl

  def getMessageListPartial(implicit request: RequestHeader): Future[HtmlPartial] =
    enhancedPartialRetriever.loadPartial(messageFrontendUrl + "/messages")

  def getMessageDetailPartial(messageToken: String)(implicit request: RequestHeader): Future[HtmlPartial] =
    enhancedPartialRetriever.loadPartial(messageFrontendUrl + "/messages/" + messageToken)

  /*def getMessageInboxLinkPartial(implicit request: RequestHeader): Future[HtmlPartial] =
    enhancedPartialRetriever.loadPartial(
      messageFrontendUrl + "/messages/inbox-link?messagesInboxUrl=" + controllers.routes.MessageController.messageList
    )*/

  def getUnreadMessageCount(implicit request: RequestHeader): Future[Option[Int]] = {
    val url = messageFrontendUrl + "/messages/count?read=No"

    val timerContext: Timer.Context =
      metrics.startTimer(MetricsEnumeration.GET_UNREAD_MESSAGE_COUNT)

    implicit val hc = headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(request)

    (for {
      messageCount <- http.GET[Option[MessageCount]](url)
    } yield {
      timerContext.stop()
      metrics.incrementSuccessCounter(MetricsEnumeration.GET_UNREAD_MESSAGE_COUNT)
      messageCount.map(_.count)
    }) recover { case e =>
      timerContext.stop()
      metrics.incrementFailedCounter(MetricsEnumeration.GET_UNREAD_MESSAGE_COUNT)
      logger.warn(s"Failed to load json", e)
      None
    }
  }
}
