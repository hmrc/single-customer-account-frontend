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

package fixtures

import akka.actor.ActorSystem
import akka.stream.Materializer
import config.FrontendAppConfig
import controllers.actions.{AuthAction, CitizenDetailsAction}
import handlers.ErrorHandler
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.{Injector, bind}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.CSRFTokenHelper.CSRFFRequestHeader
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, _}

trait SpecBase
  extends PlaySpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with Injecting
    with MockitoSugar
    with GuiceOneAppPerSuite {

  override lazy val app: Application = applicationBuilder().build()
  implicit val system: ActorSystem = ActorSystem("Test")
  implicit val materializer: Materializer = Materializer(system)
  lazy val injector: Injector = app.injector

  implicit val defaultTimeout: FiniteDuration = 5.seconds
  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(
    SessionKeys.sessionId -> "foo").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  implicit val frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  implicit val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  implicit val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit val messages: Messages = messagesApi.preferred(fakeRequest)
  implicit val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]
  lazy val messagesControllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  implicit val authAction: AuthAction = injector.instanceOf[AuthAction]
  implicit val citizenDetailsAction: CitizenDetailsAction = injector.instanceOf[CitizenDetailsAction]

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[CitizenDetailsAction].to[FakeCitizenDetailsAction],
        bind[AuthAction].to[FakeAuthAction]
      )


  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
}
