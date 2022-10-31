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

package views

import config.FrontendAppConfig
import fixtures.RetrievalOps.Ops
import models.auth.{AuthenticatedIFRequest, AuthenticatedRequest}
import models.integrationframework.PersonalDetailsResponse
import org.jsoup.nodes.Document
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.{AnyContent, AnyContentAsEmpty}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel, CredentialStrength, Enrolments}
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import views.html.HomeView

import scala.concurrent.Future

class HomeViewSpec extends  ViewSpec {

 /* lazy val home = injector.instanceOf[HomeView]

  implicit val frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val nino = "AA999999A"
  implicit val authenticatedIFRequest:  AuthenticatedIFRequest[AuthRetrievals] = AuthenticatedIFRequest[AuthRetrievals](any(),any())


  "HomeView" must {
    "Have the correct page title" in {
      implicit val result = home.apply("abc")(authenticatedIFRequest, messages, frontendAppConfig)
      lazy val document: Document = asDocument(result.toString)
      document.getElementsByTag("h1").text() must include(
        "xyz")
      //contentAsString(result) should include ("abc")
    }
  }*/
}