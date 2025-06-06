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
import org.scalatest.matchers.should.Matchers.shouldBe
import views.html.UnauthorisedView

class UnauthorisedControllerSpec extends SpecBase {

  lazy val view: UnauthorisedView             = injector.instanceOf[UnauthorisedView]
  lazy val controller: UnauthorisedController = new UnauthorisedController(messagesControllerComponents, view)

  "UnauthorisedController" must {
    "Return the unauthorised page" in
      whenReady(controller.onPageLoad(fakeRequest)) { result =>
        result.header.status shouldBe 200
      }
  }
}
