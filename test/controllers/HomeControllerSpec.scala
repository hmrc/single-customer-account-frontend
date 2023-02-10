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
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.Span
import uk.gov.hmrc.sca.services.WrapperService
import views.html.{HomeView, HomeViewWrapperVersion}

class HomeControllerSpec extends SpecBase {
  lazy val wrapperService = injector.instanceOf[WrapperService]

  lazy val homeView: HomeViewWrapperVersion = injector.instanceOf[HomeViewWrapperVersion]
  lazy val controller: HomeController = new HomeController(messagesControllerComponents, authActionInstance, ifActionInstance, homeView, wrapperService)

  "HomeController" must {
    "Return the Home page" in {

      whenReady(controller.onPageLoad(fakeRequest)) { result =>
        result.header.status shouldBe 200
      }
    }
  }

}
