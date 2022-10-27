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

package controllers

import fixtures.SpecBase
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.language.LanguageUtils

class LanguageSwitchControllerSpec extends SpecBase{

  lazy val languageUtilInstance : LanguageUtils = injector.instanceOf[LanguageUtils]
  lazy val cc : ControllerComponents = injector.instanceOf[ControllerComponents]
  lazy val controller: LanguageSwitchController = new LanguageSwitchController(frontendAppConfigInstance,languageUtilInstance, cc)

}
