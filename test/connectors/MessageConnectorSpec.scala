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

package connectors

import controllers.actions.AuthActionImpl
import fixtures.{SpecBase, WireMockHelper}
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.WireMock.{ok, urlEqualTo}
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.{AuthConnector}
import uk.gov.hmrc.play.partials.{HtmlPartial}


class MessageConnectorSpec extends SpecBase with WireMockHelper{
  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)
  lazy val connector = injector.instanceOf[MessageConnector]

  val nino = "AA999999A"

  server.start()


  applicationBuilder.configure(
      "microservice.services.integration-framework.port" -> server.port(),
      "metrics.enabled" -> false,
      "auditing.enabled" -> false,
      "auditing.traceRequests" -> false
    )
    .build()

  "Calling MessageConnector.loadPartial" must {
    "call load partial and return success partial" in {

      val returnPartial: HtmlPartial = HtmlPartial.Success(None, Html("<title>Message List</title>"))
      val url = s"http://localhost:${server.port()}/"
      server.stubFor(
        get(urlEqualTo("/")).willReturn(ok("<title>Message List</title>"))
      )
      val result = connector.loadPartial(url)(fakeRequest, ec).futureValue
      result mustBe returnPartial
    }
    "return a failed partial and log the right metrics" in {

      val returnPartial: HtmlPartial = HtmlPartial.Failure(Some(404), "Not Found")
      val url = s"http://localhost:${server.port()}/"
      server.stubFor(
        get(urlEqualTo("/")).willReturn(notFound.withBody("Not Found"))
      )
      connector.loadPartial(url)(fakeRequest,ec).futureValue mustBe returnPartial
    }
  }
}