/*
 * Copyright 2024 HM Revenue & Customs
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
import models.auth.EcoConnectorModel
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting

class EcoConnectorSpec extends PlaySpec with Matchers with Injecting with WireMockHelper {
  protected def localGuiceApplicationBuilder(): GuiceApplicationBuilder =
    GuiceApplicationBuilder()
  implicit lazy val app: Application = localGuiceApplicationBuilder().build()
  "get" must {
    "assert xxx" in {
      val ec = app.injector.instanceOf[EcoConnector]
      ec.get("2017-08-25T12:35Z", "2017-08-25T12:35Z", "NE3 4PL") mustBe EcoConnectorModel(Nil, "moderate")
    }
  }
}
