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

package utils

trait ViewSpecHelpers extends JsoupMatchers {

  // document matchers
  def haveHeadingH1WithText(expectedText: String)                  = new TagWithTextMatcher(expectedText, "h1")
  def haveHeadingH2WithText(expectedText: String)                  = new TagWithTextMatcher(expectedText, "h2")
  def haveHeadingH3WithText(expectedText: String)                  = new TagWithTextMatcher(expectedText, "h3")
  def haveHeadingH4WithText(expectedText: String)                  = new TagWithTextMatcher(expectedText, "h4")
  def haveHeadingWithText(expectedText: String)                    = new TagWithTextMatcher(expectedText, "h1")
  def haveH2HeadingWithIdAndText(id: String, expectedText: String) =
    new CssSelectorWithTextMatcher(expectedText, s"h2[id=$id]")

  def haveH2HeadingWithText(expectedText: String) = new TagWithTextMatcher(expectedText, "h2")
  def haveH3HeadingWithText(expectedText: String) = new TagWithTextMatcher(expectedText, "h3")

  def haveDescriptionTermWithIdAndText(id: String, expectedText: String) =
    new CssSelectorWithTextMatcher(expectedText, s"dt[id=$id]")
  def haveTermDescriptionWithIdAndText(id: String, expectedText: String) =
    new CssSelectorWithTextMatcher(expectedText, s"dd[id=$id]")
  def haveParagraphWithText(expectedText: String)                        = new TagWithTextMatcher(expectedText, "p")
  def haveSpanWithText(expectedText: String)                             = new TagWithTextMatcher(expectedText, "span")
  def haveListItemWithText(expectedText: String)                         = new TagWithTextMatcher(expectedText, "li")
  def haveBulletPointWithText(expectedText: String)                      = new CssSelectorWithTextMatcher(expectedText, "ul>li")
  def haveOrderedBulletPointWithText(expectedText: String)               = new CssSelectorWithTextMatcher(expectedText, "ol>li")
  def haveThWithText(expectedText: String)                               = new CssSelectorWithTextMatcher(expectedText, "th")
  def haveTdWithText(expectedText: String)                               = new CssSelectorWithTextMatcher(expectedText, "td")
  def haveCaptionWithText(expectedText: String)                          = new CssSelectorWithTextMatcher(expectedText, "caption")
  def haveContinueSubmitInput                                            = new CssSelectorWithAttributeValueMatcher("value", "Continue", "input[type=submit]")
  def haveSubmitButton(expectedText: String)                             = new CssSelectorWithTextMatcher(expectedText, "button[type=submit]")
  def haveSubmitButtonNew(expectedText: String)                          =
    new CssSelectorWithTextMatcher(expectedText, "button[class=govuk-button]")
  def haveSummaryWithText(expectedText: String)                          = new CssSelectorWithTextMatcher(expectedText, "summary")
  def haveDetailsWithText(expectedText: String)                          =
    new CssSelectorWithTextMatcher(expectedText, "div.govuk-details__text")
  def haveDetailsWithTextNew(expectedText: String)                       =
    new CssSelectorWithTextMatcher(expectedText, "details.govuk-details.hide-for-print")

  def haveDescriptionListWithId(id: String)                                                                  = new CssSelectorWithAttributeValueMatcher("id", id, "dl")
  def haveUnorderedListWithId(id: String)                                                                    = new CssSelectorWithAttributeValueMatcher("id", id, "ul")
  def haveAsideWithId(id: String)                                                                            = new CssSelectorWithAttributeValueMatcher("id", id, "aside")
  def haveSectionWithId(id: String)                                                                          = new CssSelectorWithAttributeValueMatcher("id", id, "section")
  def haveDivWithId(id: String)                                                                              = new CssSelectorWithAttributeValueMatcher("id", id, "div")
  def haveTableWithId(id: String)                                                                            = new CssSelectorWithAttributeValueMatcher("id", id, "table")
  def haveTableTheadWithId(id: String)                                                                       = new CssSelectorWithAttributeValueMatcher("id", id, "thead")
  def haveTableTdWithId(id: String)                                                                          = new CssSelectorWithAttributeValueMatcher("id", id, "td")
  def haveTableThWithIdAndText(id: String, expectedText: String)                                             =
    new CssSelectorWithTextMatcher(expectedText, s"th[id=$id]")
  def haveTableThWithClassAndText(classes: String, expectedText: String)                                     =
    new CssSelectorWithTextMatcher(expectedText, s"th[class=$classes]")
  def haveTableCaptionWithIdAndText(id: String, expectedText: String)                                        =
    new CssSelectorWithTextMatcher(expectedText, s"caption[id=$id]")
  def haveElementAtPathWithId(elementSelector: String, id: String)                                           =
    new CssSelectorWithAttributeValueMatcher("id", id, elementSelector)
  def haveElementAtPathWithText(elementSelector: String, expectedText: String)                               =
    new CssSelectorWithTextMatcher(expectedText, elementSelector)
  def haveElementAtPathWithAttribute(elementSelector: String, attributeName: String, attributeValue: String) =
    new CssSelectorWithAttributeValueMatcher(attributeName, attributeValue, elementSelector)
  def haveElementAtPathWithClass(elementSelector: String, className: String)                                 =
    new CssSelectorWithClassMatcher(className, elementSelector)
  def haveElementWithId(id: String)                                                                          = new CssSelector(s"#$id")

  def haveTableRowWithText(expectedText: String): TagWithTextMatcher = new TagWithTextMatcher(expectedText, "dt")
  def haveTableRowWithTextDescription(expectedText: String)          = new TagWithTextMatcher(expectedText, "dd")

  def haveLinkWithText(expectedText: String)                     = new CssSelectorWithTextMatcher(expectedText, "a")
  def haveErrorLinkWithText(expectedText: String)                =
    new CssSelectorWithTextMatcher(expectedText, "div.error-summary>ul>li>a")
  def haveErrorLinkWithTextNew(expectedText: String)             =
    new CssSelectorWithTextMatcher(expectedText, "div.govuk-error-summary__body>ul>li>a")
  def haveClassWithText(expectedText: String, className: String) =
    new CssSelectorWithTextMatcher(expectedText, s".$className")
  def haveClassCount(expectedClass: String, expectedCount: Int)  =
    new CssSelectorWithTextCount(s".$expectedClass", expectedCount)

  def haveBackButtonWithUrl(expectedURL: String)                     = new IdSelectorWithUrlMatcher(expectedURL, "backLink")
  def haveCancelLinkWithUrl(expectedURL: String)                     = new IdSelectorWithUrlMatcher(expectedURL, "cancelLink")
  def haveLinkWithUrlWithID(id: String, expectedURL: String)         = new IdSelectorWithUrlMatcher(expectedURL, id)
  def haveLinkWithUrlWithClass(classes: String, expectedURL: String) =
    new ClassSelectorWithUrlMatcher(expectedURL, classes)

  // element matchers
  def haveText(expectedText: String)   = new ElementWithTextMatcher(expectedText)
  def haveLinkURL(expectedUrl: String) = new ElementWithAttributeValueMatcher(expectedUrl, "href")
  def haveClass(expectedClass: String) = new ElementWithClassMatcher(expectedClass)

  def haveLinkElement(id: String, href: String, text: String) = new IdSelectorWithUrlAndTextMatcher(id, href, text)

  def haveInputLabelWithText(id: String, expectedText: String) =
    new CssSelectorWithTextMatcher(expectedText, s"label[for=$id]")

  def haveHintWithText(id: String, expectedText: String) =
    new CssSelectorWithTextMatcher(expectedText, s"div.govuk-hint[id=$id]")

  def haveStrongWithText(expectedText: String) = new CssSelectorWithTextMatcher(expectedText, "strong")

  def havePanelWithBodyText(expectedText: String) =
    new CssSelectorWithTextMatcher(expectedText, ".govuk-panel__body")

  def havePanelWithHeaderText(expectedText: String) =
    new CssSelectorWithTextMatcher(expectedText, ".govuk-panel__title")
}
