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

import connectors.CapabilityConnector
import fixtures.SpecBase
import models.integrationframework.CapabilityDetails
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

class CapabilityServiceSpec extends SpecBase with ScalaFutures with BeforeAndAfterEach {

  private val mockCapabilityConnector = mock[CapabilityConnector]

  override lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", s"/capability-details/")
  val nino = domain.Nino("GG012345C")

  val service = new CapabilityService(mockCapabilityConnector)

  override protected def beforeEach(): Unit = {
    Mockito.reset(mockCapabilityConnector)
  }

  "getCapabilityDetails" must {

    "return successful response if getCapabilityDetails returns some data" in {

      val capabilityDetails: Seq[CapabilityDetails] = Seq(
        CapabilityDetails(
          nino = Nino(true, Some("GG012345C")),
          date = LocalDate.of(2022, 5, 19),
          descriptionContent = "Desc-1",
          url = "url-1"),
        CapabilityDetails(
          nino = Nino(true, Some("GG012345C")),
          date = LocalDate.of(2023, 4, 9),
          descriptionContent = "Desc-2",
          url = "url-2")
      )

      when(mockCapabilityConnector.getCapabilityDetails(any()))
        .thenReturn(Future.successful(capabilityDetails))

      service.getCapabilityDetails(nino).map(res =>
        res mustBe capabilityDetails
      )

    }

    "throw an exception when the capability details are not found" in {

      val nino = domain.Nino("GG012345C")

      when(mockCapabilityConnector.getCapabilityDetails(nino)).thenReturn(Future.successful(Seq.empty))

      service.getCapabilityDetails(nino).map(res =>
        res mustBe Seq.empty
      )
    }
  }
}
