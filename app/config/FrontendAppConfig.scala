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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  val appName: String = configuration.get[String]("appName")

  val loginUrl: String               = configuration.get[String]("urls.login")
  val loginContinueUrl: String       = configuration.get[String]("urls.loginContinue")
  val signOutUrl: String             = configuration.get[String]("urls.signOut")
  val carbonIntensityBaseUrl: String = configuration.get[String]("urls.carbon-intensity.base")

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en"),
    "cy" -> Lang("cy")
  )

  val timeout: Int   = configuration.get[Int]("sca-wrapper.timeout-dialog.timeout")
  val countdown: Int = configuration.get[Int]("sca-wrapper.timeout-dialog.countdown")
}
