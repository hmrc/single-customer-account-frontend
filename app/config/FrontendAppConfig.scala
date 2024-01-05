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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, servicesConfig: ServicesConfig) {

  val host: String    = configuration.get[String]("sca-wrapper.host")
  val appName: String = configuration.get[String]("appName")

  val integrationFrameworkUrl: String = servicesConfig.baseUrl(serviceName = "integration-framework")
  val integrationFrameworkAuthToken: String = configuration.get[String]("microservice.services.integration-framework.authorization-token")
  val integrationFrameworkEnvironment: String = configuration.get[String]("microservice.services.integration-framework.environment")

  val nispBaseUrl: String = configuration.get[String]("microservice.services.nisp-frontend.url")
  val niRecordUrl : String = s"$nispBaseUrl/check-your-state-pension/account/nirecord"
  val spSummaryUrl : String = s"$nispBaseUrl/check-your-state-pension/account"

  val loginUrl: String         = configuration.get[String]("urls.login")
  val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  val signOutUrl: String       = configuration.get[String]("urls.signOut")

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en"),
    "cy" -> Lang("cy")
  )

  lazy private val exitSurveyServiceName = "single-customer-account-frontend"
  lazy private val contactBaseUrl: String = configuration.get[String]("microservice.services.contact-frontend.url")

  def feedbackUrl(implicit request: RequestHeader): String =
    s"$contactBaseUrl/contact/beta-feedback?service=$exitSurveyServiceName&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  val timeout: Int = configuration.get[Int]("sca-wrapper.timeout-dialog.timeout")
  val countdown: Int = configuration.get[Int]("sca-wrapper.timeout-dialog.countdown")
}
