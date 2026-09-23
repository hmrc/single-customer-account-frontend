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

package controllers

import connectors.PertaxConnector
import fixtures.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.OK
import play.api.test.Helpers.*
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class PertaxControllerSpec extends SpecBase {

  private val mockPertaxConnector = mock[PertaxConnector]

  private lazy val controller: PertaxController =
    new PertaxController(messagesControllerComponents, mockPertaxConnector)

  "authorise" must {
    "return the status and body returned by the pertax connector" in {
      when(mockPertaxConnector.authorise()(any[HeaderCarrier], any[ExecutionContext]))
        .thenReturn(Future.successful(HttpResponse(OK, """{"code":"OK","message":"Access granted"}""")))

      val result = controller.authorise(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe """{"code":"OK","message":"Access granted"}"""
    }
  }
}
