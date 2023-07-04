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

import connectors.ActivitiesConnector
import fixtures.SpecBase
import models.integrationframework.{Activities, CapabilityDetails}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain

import java.time.LocalDate
import scala.concurrent.Future

class ActivitiesServiceSpec extends SpecBase with ScalaFutures with BeforeAndAfterEach {

  private val mockActivitiesConnector = mock[ActivitiesConnector]

  override lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", s"/capability-details/")
  val nino = domain.Nino("GG012345C")

  val service = new ActivitiesService(mockActivitiesConnector)

  override protected def beforeEach(): Unit = {
    Mockito.reset(mockActivitiesConnector)
  }

  "getActivities" must {

    "return successful response if getActivities returns some data" in {

      val activities: Activities = Activities(
        Seq(CapabilityDetails(
          nino = Nino(true, Some("GG012345C")),
          date = LocalDate.of(2022, 5, 19),
          descriptionContent = "Desc-1",
          url = "url-1",
          activityHeading = "Your Tax code has changed"),
          CapabilityDetails(
            nino = Nino(true, Some("GG012345C")),
            date = LocalDate.of(2023, 4, 9),
            descriptionContent = "Desc-2",
            url = "url-2",
            activityHeading = "Your Tax code has changed")
        ),
        None,
        Seq.empty,
        Seq.empty)

      when(mockActivitiesConnector.getActivities(any()))
        .thenReturn(Future.successful(activities))

      service.getActivities(nino).map(res =>
        res mustBe activities
      )
    }

    "throw an exception when activities are not found" in {

      val nino = domain.Nino("GG012345C")

      when(mockActivitiesConnector.getActivities(nino)).thenReturn(Future.successful(Activities(Seq.empty, None, Seq.empty, Seq.empty)))

      service.getActivities(nino).map(res =>
        res mustBe Activities(Seq.empty, None, Seq.empty, Seq.empty)
      )
    }
  }
}
