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

import connectors.MessageConnector
import controllers.actions.AuthActionImpl
import fixtures.{SpecBase, WireMockHelper}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.play.partials.{HeaderCarrierForPartialsConverter, HtmlPartial}

import scala.concurrent.Future

class MessageServiceSpec extends SpecBase with WireMockHelper {

  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)
  val mockMessageConnector= mock[MessageConnector]
  lazy val fakeHttp = injector.instanceOf[HttpClient]
  lazy val fakeheadercarrier = injector.instanceOf[HeaderCarrierForPartialsConverter]
  lazy val messageService: MessageService = new MessageService(fakeHttp,fakeheadercarrier,frontendAppConfigInstance,mockMessageConnector)
  val nino = "AA999999A"

  server.start()


  applicationBuilder.configure(
    "microservice.services.contact-frontend.port" -> server.port(),
    "metrics.enabled" -> false,
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false
  )
    .build()


  /*"Calling getMessageListPartial" must {
    "return message partial for list of messages" in {

      val expected = HtmlPartial.Success(Some("Success"), Html("Your Messages</h1>"))
      when(mockMessageConnector.loadPartial(any())(any(), any())).thenReturn(
        Future.successful(expected)
      )

      val result = messageService.getMessageListPartial(fakeRequest,hc).futureValue
      result.successfulContentOrEmpty.toString() must include ("<h1 class=\"govuk-heading-l heading-xlarge\">Your Messages</h1>")
    }
  }

  "Calling getMessageDetailPartial" must {
    "return message partial for message details" in {
      val expected = HtmlPartial.Success.apply(Some("test"), Html("<h1>Reminder to file a Self Assessment return</h1>\n   <p>This message was sent to you on 22 November 2022</p>\n   \n   \n   <p>This is a test message</p>"))
      when(mockMessageConnector.loadPartial(any())(any(), any())).thenReturn(
        Future.successful(expected)
      )
      val result = messageService.getMessageDetailPartial("abcd")(FakeRequest(),hc).futureValue
      result.successfulContentOrEmpty.toString() must include ("<h1>Reminder to file a Self Assessment return</h1>")

    }
  }*/
}
