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

package controllers.action

import controllers.actions.{DataRetrievalAction, DataRetrievalActionImpl}
import fixtures.SpecBase
import models.auth.SessionRequest
import models.session.{OptionalDataRequest, Session}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import repositories.SessionRepository

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.Future

class DataRetrievalActionClass extends SpecBase{


  /*class Harness(sessionRepository: SessionRepository) extends DataRetrievalActionImpl(sessionRepository) {
    def callTransform[A](request: SessionRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "calling data retrieval from session repository" must {

    "must leave the request unfiltered" in {
      val sessionRepository = mock[SessionRepository]
      when(sessionRepository.get("id")) thenReturn Future(Option(Session("id", any(), Some("abc"))))
      val action = new Harness(sessionRepository)

      val result = action.callTransform(SessionRequest(FakeRequest(), "id")).futureValue

      result.session.get("id") mustBe true
    }
  }*/
}
