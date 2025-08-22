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

package fixtures

import config.FrontendAppConfig
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, BodyParsers, MessagesControllerComponents}
import play.api.test.CSRFTokenHelper.CSRFFRequestHeader
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, ~}
import uk.gov.hmrc.domain
import uk.gov.hmrc.domain.Generator
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.sca.models.{PtaMinMenuConfig, WrapperDataResponse}
import uk.gov.hmrc.sca.utils.Keys

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, *}
import scala.util.Random

trait SpecBase
    extends PlaySpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with Injecting
    with MockitoSugar
    with GuiceOneAppPerSuite {

  override lazy val app: Application      = applicationBuilder().build()
  implicit val system: ActorSystem        = ActorSystem("Test")
  implicit val materializer: Materializer = Materializer(system)
  lazy val injector: Injector             = app.injector

  implicit val defaultTimeout: FiniteDuration               = 5.seconds
  implicit val hc: HeaderCarrier                            = HeaderCarrier()
  implicit val ec: ExecutionContext                         = injector.instanceOf[ExecutionContext]
  implicit val frontendAppConfigInstance: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
    .withSession(SessionKeys.sessionId -> "foo")
    .withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val testTrustedHelper =
    TrustedHelper("principalName", "attorneyName", "url", Some(new Generator(new Random).nextNino.nino))

  lazy val fakeRequestWithTrustedHelper: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
    .addAttr(
      Keys.wrapperDataKey,
      WrapperDataResponse(
        Nil,
        PtaMinMenuConfig("", ""),
        Nil,
        Nil,
        Some(0),
        Some(testTrustedHelper)
      )
    )
    .withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
    .withSession(SessionKeys.sessionId -> "foo")
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val messagesApiInstance: MessagesApi                           = injector.instanceOf[MessagesApi]
  lazy val messages: Messages                                         = messagesApiInstance.preferred(fakeRequest)
  lazy val messagesControllerComponents: MessagesControllerComponents =
    injector.instanceOf[MessagesControllerComponents]
  lazy val bodyParserInstance: BodyParsers.Default                    = injector.instanceOf[BodyParsers.Default]

  type AuthRetrievals =
    Option[String] ~ AffinityGroup ~ Enrolments ~ Option[Credentials] ~ Option[String] ~ ConfidenceLevel ~
      Option[Name] ~ Option[TrustedHelper]

  def fakeSaEnrolments(utr: String, enrolmentState: String): Set[Enrolment] = Set(
    Enrolment("IR-SA", Seq(EnrolmentIdentifier("UTR", utr)), enrolmentState)
  )

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()

  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
}
