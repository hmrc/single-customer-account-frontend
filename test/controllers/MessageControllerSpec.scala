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

package controllers

import controllers.actions.AuthActionImpl
import fixtures.RetrievalOps.Ops
import fixtures.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.{MessagesControllerComponents, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.MessageService
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel, CredentialStrength, Enrolments}
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.play.partials.HtmlPartial
import views.html.{MessageDetailView, MessageInboxView}

import scala.concurrent.Future

class MessageControllerSpec extends SpecBase {

  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)
  val mockMessageService = mock[MessageService]
  lazy val viewInbox: MessageInboxView = injector.instanceOf[MessageInboxView]
  lazy val viewDetail: MessageDetailView = injector.instanceOf[MessageDetailView]
  lazy val controller: MessageController = new MessageController(mockMessageService, authAction, ifActionInstance, messagesControllerComponents, viewInbox, viewDetail)
  val nino = "AA999999A"

  "Calling MessageController.messageList" must {
    "call messages and return 200" in {
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(
        Some(nino) ~
          Individual ~
          Enrolments(fakeSaEnrolments("1632631936", "Activated")) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.strong) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )
      when(mockMessageService.getMessageListPartial(any())) thenReturn {
        Future(HtmlPartial.Success(Some("Success"), Html("<title>Message List</title>")))
      }
      val result = controller.messageList(FakeRequest())
      contentAsString(result) must include("Message List")
      whenReady(result) { res =>
        res.header.status shouldBe 200
      }
    }

    "call messages and return 400" in {
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(
        Some(nino) ~
          Individual ~
          Enrolments(Set.empty) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.weak) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999B")) ~
          Some("profileUrl")
      )
      when(mockMessageService.getMessageListPartial(any())) thenReturn {
        Future(HtmlPartial.Failure(None))
      }
      val result = controller.messageList(FakeRequest())
      contentAsString(result) must include("Sorry, there has been a technical problem retrieving your messages")


    }
  }

  "Calling MessageController.messageDetail" must {
    "call messages and return 200 when clicked on message" in {
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(
        Some(nino) ~
          Individual ~
          Enrolments(fakeSaEnrolments("1632631936", "Activated")) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.strong) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )
      when(mockMessageService.getMessageDetailPartial(any())(any())) thenReturn {
        Future(HtmlPartial.Success(Some("Success"), Html("<title/>")))
      }
      val result = controller.messageDetail("SOME-MESSAGE-TOKEN")(FakeRequest("GET", "/foo"))

      whenReady(result) { res =>
        res.header.status shouldBe 200
      }
      contentAsString(result) must include("<title/>")
    }

    "call messages and return 400 when clicked on message" in {
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(
        Some(nino) ~
          Individual ~
          Enrolments(Set.empty) ~
          Some(Credentials("id", "type")) ~
          Some(CredentialStrength.strong) ~
          ConfidenceLevel.L200 ~
          Some(Name(Some("chaz"), Some("dingle"))) ~
          Some(TrustedHelper("name", "name", "link", "AA999999A")) ~
          Some("profileUrl")
      )
      when(mockMessageService.getMessageDetailPartial(any())(any())) thenReturn {
        Future(HtmlPartial.Failure(None, ""))
      }
      val result = controller.messageDetail("")(FakeRequest("GET", "/foo"))
      whenReady(result) { res =>
        res.header.status shouldBe 400
      }
      contentAsString(result) must include("<title/>")
    }
  }
}