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

package actions

import controllers.actions.{AuthAction, AuthActionImpl}
import fixtures.{SpecBase, TestData}
import models.auth.AuthenticatedRequest
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, AnyContentAsEmpty, BodyParser, BodyParsers, EssentialAction, Request, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.govukfrontend.views.viewmodels.cookiebanner.Action
import views.html.YourTaxesAndBenefits

import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockBodyParser: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]

  lazy val action = new AuthActionImpl(mockAuthConnector, frontendAppConfig, mockBodyParser)

  val action:

  "x" must {
    "y" in {

    }
  }

}
