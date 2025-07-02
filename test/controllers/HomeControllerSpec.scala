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
import uk.gov.hmrc.hmrcfrontend.config.AccessibilityStatementConfig
import uk.gov.hmrc.sca.services.WrapperService
import views.html.HomeViewWrapperVersion

class HomeControllerSpec extends SpecBase {
  private lazy val wrapperService: WrapperService   = injector.instanceOf[WrapperService]
  private lazy val accessibilityStatementConfig     = injector.instanceOf[AccessibilityStatementConfig]
  private lazy val homeView: HomeViewWrapperVersion = injector.instanceOf[HomeViewWrapperVersion]
  private lazy val controller: HomeController       =
    new HomeController(
      messagesControllerComponents,
      authActionInstance,
      homeView,
      wrapperService,
      accessibilityStatementConfig
    )

  "HomeController" must {
    "Return the Home page using deprecated library call" in
      whenReady(controller.oldWrapperLayout(fakeRequest)) { result =>
        result.header.status shouldBe 200
      }

    "Return the Home page using library call" in
      whenReady(controller.newWrapperLayout(fakeRequest)) { result =>
        result.header.status shouldBe 200
      }
  }
}
