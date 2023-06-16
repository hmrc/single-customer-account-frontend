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

package views

import fixtures.SpecBase
import models.integrationframework.{ActionDetails, Actions}
import org.jsoup.Jsoup
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.domain
import utils.ViewSpecHelpers
import views.html.ActionsView

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActionsViewSpec extends SpecBase with ViewSpecHelpers {


  val nino = domain.Nino("GG012345C")
  val ninoT = Nino(hasNino = true, Some("GG012345C"))


  private lazy val activityModel = Actions(
    taxCalc = Seq(
      ActionDetails(
        nino = Nino(true, Some("GG012345C")),
        date = LocalDate.now.minusMonths(2).minusDays(1),
        descriptionContent = "You paid too much tax in the 2022 to 2023 tax year. HMRC owes you a £84.23 refund",
        actionDescription = "Claim your tax refund",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison",
        activityHeading = "Things for you to do"),
    )
  )

  private lazy val emptyActivityModel = Actions(
    taxCalc = Seq.empty,
  )

  private val template: ActionsView = inject[ActionsView]
  implicit val request: Request[_] = FakeRequest()

  def actionsView: Html = template(activityModel)(request, messages)

  def emptyActionsView: Html = template(emptyActivityModel)(request, messages)

  private lazy val actionsDoc = Jsoup.parse(actionsView.toString())
  private lazy val emptyActionsDoc = Jsoup.parse(emptyActionsView.toString())

  println("AC: " + actionsDoc)

  "Actions View" must {

    "display things for you to do heading" when {
      "actions are present" in
        actionsDoc must haveH2HeadingWithText("Things for you to do")
    }
  }

  "display description list" when {
    "actions are present" in {
      actionsDoc must haveDescriptionListWithId("actionsList")
      actionsDoc must haveLinkWithUrlWithClass(
        "govuk-link--no-visited-state", "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison")
      val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      actionsDoc must haveStrongWithText(
        LocalDate.now.minusMonths(2).minusDays(1).format(dateTimeFormatter))
      actionsDoc must haveParagraphWithText("You paid too much tax in the 2022 to 2023 tax year. HMRC owes you a £84.23 refund")
      actionsDoc must haveLinkWithText("Claim your tax refund")
    }
  }

  "display no Actions" when {
    "no actions are present" in {
      emptyActionsDoc mustNot haveH2HeadingWithText("Things for you to do")
      emptyActionsDoc mustNot haveDescriptionListWithId("actionsList")
      emptyActionsDoc mustNot haveLinkWithUrlWithClass(
        "govuk-link--no-visited-state", "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison")
      val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      emptyActionsDoc mustNot haveStrongWithText(
        LocalDate.now.minusMonths(2).minusDays(1).format(dateTimeFormatter))
      emptyActionsDoc mustNot haveParagraphWithText("You paid too much tax in the 2022 to 2023 tax year. HMRC owes you a £84.23 refund")
      emptyActionsDoc mustNot haveLinkWithText("Claim your tax refund")
    }
  }
}
