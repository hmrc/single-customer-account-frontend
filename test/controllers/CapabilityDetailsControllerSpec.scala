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

import fixtures.SpecBase
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import services.CapabilityService
import views.html.CapabilityDetailsView


class CapabilityDetailsControllerSpec extends SpecBase {
  lazy val capabilityService = injector.instanceOf[CapabilityService]

  lazy val capabilityDetailsView: CapabilityDetailsView = injector.instanceOf[CapabilityDetailsView]
//lazy val controller: CapabilityDetailsController = new CapabilityDetailsController(messagesControllerComponents,capabilityService, capabilityDetailsView)
  lazy val controller: CapabilityDetailsController = injector.instanceOf[CapabilityDetailsController]
  val expectedDate = "9 April 2023"
  val expectedDescription = "Your tax code has changed"
  val expectedUrl = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"

  "CapabilityDetailsController" must {
    "Return the capabilityDetails page" in {

      whenReady(controller.getCapabilitiesData(fakeRequest)) { result =>
        result.header.status shouldBe 200
      }
    }

    "Return correct data on capabilityDetails page" in {
      val result = controller.getCapabilitiesData(fakeRequest)
      contentAsString(result) must include(expectedDate)
      contentAsString(result) must include(expectedDescription)
      contentAsString(result) must include(expectedUrl)

    }
  }
}
