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

package repositories

import config.FrontendAppConfig
import fixtures.SpecBase
import models.session.Session
import org.mockito.Mockito.when
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.time.{Clock, Instant, LocalDateTime, ZoneId}



class SessionRepositorySpec extends SpecBase with DefaultPlayMongoRepositorySupport[Session]{

 private val instant = Instant.now()
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)
  private val session = Session("id", LocalDateTime.now(), Some("abc"))

  private val mockAppConfig = mock[FrontendAppConfig]
  when(mockAppConfig.cacheTtl) thenReturn 1

  lazy val repository = new SessionRepository(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    clock = stubClock
  )

  " calling set method" must {

    "must set the last updated time on the supplied session to `now`, and save them" in {

      val setResult = repository.set(session).futureValue

      setResult mustEqual true
    }
  }

 /* "calling get" must {

      "must return the session based on id" in {

        insert(session).futureValue
        val expectedResult = session copy ("id", LocalDateTime.now(), Some("abc"))
        val result = repository.get("id").futureValue

        result mustEqual expectedResult
      }
    }*/

    " calling get when there is no record for this id" must {

      "must return None" in {

        repository.get("id that does not exist").futureValue must not be defined
      }
    }


  "calling clear" must {

    "must remove a record" in {

      insert(session).futureValue

      val result = repository.clear(session.id).futureValue

      result mustEqual true
      repository.get(session.id).futureValue must not be defined
    }

    "must return true when there is no record to remove" in {
      val result = repository.clear("id that does not exist").futureValue

      result mustEqual true
    }
  }

  " calling keepAlive when there is record" must {


      "must return true" in {

        insert(session).futureValue

        val result = repository.keepAlive(session.id).futureValue
        result mustEqual true
      }
    }

    "calling keep alive when there is no record for this id" must {

      "must return true" in {

        repository.keepAlive("id that does not exist").futureValue mustEqual true
      }
    }

}
