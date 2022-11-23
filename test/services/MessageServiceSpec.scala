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

import com.github.tomakehurst.wiremock.client.WireMock.{get, ok, urlEqualTo}
import connectors.MessageConnector
import controllers.actions.AuthActionImpl
import fixtures.RetrievalOps.Ops
import fixtures.{SpecBase, WireMockHelper}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel, CredentialStrength, Enrolments}
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


  "Calling getMessageListPartial" must {
    "return message partial for list of messages" in {

      val expected = HtmlPartial.Success(Some("Success"), Html("<title>Message List</title>"))
      when(mockMessageConnector.loadPartial(any())(any(), any())).thenReturn(
        Future.successful(expected)
      )

      val result = messageService.getMessageListPartial(fakeRequest,any()).futureValue
      result mustBe expected
    }
  }

  "Calling getMessageDetailPartial" must {
    "return message partial for message details" in {
      val expected = HtmlPartial.Success.apply(None, Html("body"))
      when(mockMessageConnector.loadPartial(any())(any(), any())).thenReturn(
        Future.successful(expected)
      )
      val result = messageService.getMessageDetailPartial("abcd")(FakeRequest(),hc).futureValue
      result mustBe expected

    }
  }
}
