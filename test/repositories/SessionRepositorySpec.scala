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

package repositories

import fixtures.{SpecBase, WireMockHelper}
import play.api.http.Status.OK
import play.api.test.Helpers.status

import scala.concurrent.Future

class SessionRepositorySpec extends SpecBase with WireMockHelper{

  lazy val repository = injector.instanceOf[SessionRepository]

  /*"Session repository.keepAlive" must {

    "return 200" in {

      val result: Future[Boolean] = repository.keepAlive(id = "id")

      result mustBe Future[Boolean]
    }
  }*/
}
