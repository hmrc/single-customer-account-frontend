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
import controllers.actions.{AuthAction, CitizenDetailsAction, DataRetrievalAction}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.BaseOneAppPerTest
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.test.{FakeRequest, Injecting}
import repositories.SessionRepository

trait SpecBase
  extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with Injecting
    with BaseOneAppPerTest {

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[AuthAction].to[FakeIdentifierAction],
        bind[CitizenDetailsAction].to[FakeAuthAction]
      )
  override lazy val app: Application = applicationBuilder().build()

  implicit val system: ActorSystem = ActorSystem("Sys")
  implicit val materializer: Materializer = Materializer(system)
  lazy val injector: Injector = app.injector

  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
}
