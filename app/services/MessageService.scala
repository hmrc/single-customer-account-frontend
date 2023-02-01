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

import config.FrontendAppConfig
import connectors.MessageConnector
import models.MessageCount
import play.api.Logging
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.play.partials.{HeaderCarrierForPartialsConverter, HtmlPartial}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MessageService @Inject()(
                                         http: HttpClient,
                                         headerCarrierForPartialsConverter: HeaderCarrierForPartialsConverter,
                                         servicesConfig: FrontendAppConfig,
                                         enhancedPartialRetriever: MessageConnector
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

 /* def getUnreadMessageCount(implicit request: RequestHeader): Future[Option[Int]] = {
    val url = messageFrontendUrl + "/messages/count?read=No"
    implicit val hc: HeaderCarrier = headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(request)

    (for {
      messageCount <- http.GET[Option[MessageCount]](url)
    } yield {
      messageCount.map(_.count)
    }) recover { case exception =>
      logger.error(s"Failed to load json", exception)
      None
    }
  }*/
}
