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

package connectors

import fixtures.WireMockHelper
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import uk.gov.hmrc.domain.{Generator, Nino}
import uk.gov.hmrc.http.{HeaderCarrier, RequestId, SessionId}

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.util.Random

trait ConnectorBaseSpec
    extends PlaySpec
    with MockitoSugar
    with WireMockHelper
    with ScalaFutures
    with Injecting
    with IntegrationPatience {

  val nino: Nino = new Generator(new Random).nextNino

  val sessionId = "testSessionId"
  val requestId = "testRequestId"

  protected def localGuiceApplicationBuilder(): GuiceApplicationBuilder = GuiceApplicationBuilder()

  implicit lazy val app: Application = localGuiceApplicationBuilder().build()

  override def beforeEach(): Unit = {
    super.beforeEach()
    ()
  }

  implicit val hc: HeaderCarrier = HeaderCarrier(
    sessionId = Some(SessionId(sessionId)),
    requestId = Some(RequestId(requestId))
  )

  implicit lazy val ec: ExecutionContext = inject[ExecutionContext]

}
