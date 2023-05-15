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
import models.integrationframework.IfCapabilityDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain

import scala.concurrent.Future

class CapabilityServiceSpec extends SpecBase with ScalaFutures with BeforeAndAfterEach {

  private val mockCapabilityConnector = mock[CapabilityConnector]

  override lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", s"/capability-details/")
  val nino = domain.Nino("GG012345C")
  val expectedNino = Nino(hasNino = true,Some("GG012345C"))
  val expectedDate = "9 April 2023"
  val expectedDescription = "Your tax code has changed"
  val expectedUrl = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"

  val service = new CapabilityService(mockCapabilityConnector)

  override protected def beforeEach(): Unit = {
    Mockito.reset(mockCapabilityConnector)
  }

  "getCapabilityDetails" must {

    "return successful response if getCapabilityDetails returns some data" in {

      val expectedObj = IfCapabilityDetails(
       nino = expectedNino,
       date = expectedDate,
       descriptionContent = expectedDescription,
       url = expectedUrl
      )

      when(mockCapabilityConnector.getCapabilityDetails(any()))
        .thenReturn(Future.successful(Some(expectedObj)))


      service.getCapabilityDetails(nino).futureValue shouldBe expectedObj

    }

    "throw an exception when the capability details are not found" in {

      val nino = domain.Nino("GG012345C")

      when(mockCapabilityConnector.getCapabilityDetails(nino)).thenReturn(Future.successful(None))

      val result = service.getCapabilityDetails(nino)

      assertThrows[RuntimeException] {
        result.futureValue
      }
    }
  }
}
