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
import models.integrationframework.{Activities, CapabilityDetails}
import org.jsoup.Jsoup
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.Nino
import utils.ViewSpecHelpers
import views.html.ActivitiesView

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActivitiesViewSpec extends SpecBase with ViewSpecHelpers {


  val hasNino = true
  private lazy val activityModel = Activities(


    taxCalc = Seq(
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.minusMonths(2).minusDays(1),
        descriptionContent = "Your tax calculation for the 2022-2023 is now available",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
    ),
    taxCode = Seq(
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.minusMonths(1).minusDays(1),
        descriptionContent = "Your tax code has changed - 1",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.minusMonths(2),
        descriptionContent = "Your tax code has changed - 2",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now,
        descriptionContent = "Your tax code has changed - 7",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
    ),
    childBenefit = Seq(
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.minusMonths(1).minusDays(1),
        descriptionContent = "HMRC paid you Child Benefit",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.minusMonths(2),
        descriptionContent = "HMRC paid you Child Benefit",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.minusMonths(3).plusDays(1),
        descriptionContent = "HMRC paid you Child Benefit",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.withMonth(4).withDayOfMonth(5),
        descriptionContent = "HMRC paid you Child Benefit",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.withMonth(4).withDayOfMonth(6),
        descriptionContent = "HMRC paid you Child Benefit",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
    ),
    payeIncome = Seq(
      CapabilityDetails(
        nino = Nino(hasNino, Some("GG012345C")),
        date = LocalDate.now.minusMonths(2).minusDays(14),
        descriptionContent = "Central Perk Coffee Ltd paid you PAYE income",
        url = "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison"),
    )
  )

  private val template: ActivitiesView = inject[ActivitiesView]
  implicit val request: Request[_] = FakeRequest()

  def activitiesView: Html = template(activityModel)(request, messages)

  private lazy val activitiesDoc = Jsoup.parse(activitiesView.toString())

  "Activities View" must {

    "display headings" when {
      "activities are present" in {
        activitiesDoc must haveHeadingH1WithText("Your recent account activity")
        activitiesDoc must haveH2HeadingWithText("Your tax calculation")
        activitiesDoc must haveH2HeadingWithText("Latest Tax code change")
        activitiesDoc must haveH2HeadingWithText("Recent Child Benefit payments")
        activitiesDoc must haveH2HeadingWithText("Your PAYE income for the current tax year")
        activitiesDoc must haveClassWithText(
          "Your PAYE income for the current tax year","govuk-heading-m"
        )
      }
    }

    "display description list" when {
      "activities are present" in {
        activitiesDoc must haveDescriptionListWithId("actionsList")
        activitiesDoc must haveLinkWithUrlWithClass(
          "govuk-link--no-visited-state", "www.tax.service.gov.uk/check-income-tax/tax-code-change/tax-code-comparison")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        activitiesDoc must haveLinkWithText("Central Perk Coffee Ltd paid you PAYE income")
        activitiesDoc must haveStrongWithText(
          LocalDate.now.minusMonths(2).minusDays(14).format(dateTimeFormatter))
        activitiesDoc must haveElementAtPathWithClass("div","govuk-summary-list__row")
        activitiesDoc must haveElementAtPathWithClass("dt","govuk-summary-list__key")
        activitiesDoc must haveElementAtPathWithClass("dd","govuk-summary-list__value")
      }
    }
  }
}
