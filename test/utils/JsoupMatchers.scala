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

import org.jsoup.nodes.{Attributes, Document, Element}
import org.jsoup.select.Elements
import org.scalatest.matchers.{MatchResult, Matcher}
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala

trait JsoupMatchers {

  class TagWithTextMatcher(expectedContent: String, tag: String) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val elements: List[String] =
        left
          .getElementsByTag(tag)
          .toList
          .map(_.text)

      lazy val elementContents = elements.mkString("\t", "\n\t", "")

      MatchResult(
        elements.contains(expectedContent),
        s"[$expectedContent] not found in '$tag' elements:[\n$elementContents]",
        s"'$tag' element found with text [$expectedContent]"
      )
    }
  }

  class CssSelectorWithTextMatcher(expectedContent: String, selector: String) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val elements: List[String] =
        left
          .select(selector)
          .toList
          .map(_.text)

      lazy val elementContents = elements.mkString("\t", "\n\t", "")

      MatchResult(
        elements.contains(expectedContent),
        s"[$expectedContent] not found in elements with '$selector' selector:[\n$elementContents]",
        s"[$expectedContent] element found with '$selector' selector and text [$expectedContent]"
      )
    }
  }

  class CssSelectorWithTextCount(selector: String, expectedCount: Int) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val elements: List[String] =
        left
          .select(selector)
          .toList
          .map(_.text)

      MatchResult(
        elements.length == expectedCount,
        s"[$expectedCount] not met in elements with '$selector' selector. Actual count:[${elements.length}]",
        s"[$expectedCount] met in elements with '$selector' selector"
      )
    }
  }

  class TagWithIdAndTextMatcher(expectedContent: String, tag: String, id: String)
    extends CssSelectorWithTextMatcher(expectedContent, s"$tag[id=$id]")

  class CssSelectorWithAttributeValueMatcher(attributeName: String, attributeValue: String, selector: String)
    extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val attributes: List[Attributes] =
        left
          .select(selector)
          .toList
          .map(_.attributes())

      lazy val attributeContents = attributes.mkString("\t", "\n\t", "")

      MatchResult(
        attributes.map(_.get(attributeName)).contains(attributeValue),
        s"[$attributeName=$attributeValue] not found in elements with '$selector' selector:[\n$attributeContents]",
        s"[$attributeName=$attributeValue] element found with '$selector' selector"
      )
    }
  }

  class CssSelectorWithClassMatcher(className: String, selector: String) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val classes: List[String] =
        left
          .select(selector)
          .toList
          .map(_.className())

      lazy val classContents = classes.mkString("\t", "\n\t", "")

      MatchResult(
        classes.exists(_.contains(className)),
        s"[class=$className] not found in elements with '$selector' selector:[\n$classContents]",
        s"[class=$className] element found with '$selector' selector"
      )
    }
  }

  class CssSelector(selector: String) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val elements: Elements =
        left.select(selector)

      MatchResult(
        elements.size >= 1,
        s"No element found with '$selector' selector",
        s"${elements.size} elements found with '$selector' selector"
      )
    }
  }

  class IdSelectorWithTextMatcher(expectedContent: String, selector: String) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val elements: String =
        left.getElementById(selector).text

      lazy val elementContents = elements.mkString("\t", "\n\t", "")

      MatchResult(
        elements.contains(expectedContent),
        s"[$expectedContent] not found in elements with '$selector' selector:[\n$elementContents]",
        s"[$expectedContent] element found with '$selector' selector and text [$expectedContent]"
      )
    }
  }

  class IdSelectorWithUrlMatcher(expectedContent: String, selector: String) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val elements: String =
        left.getElementById(selector).attr("href")

      lazy val elementContents = elements.mkString("\t", "\n\t", "")

      MatchResult(
        elements.contains(expectedContent),
        s"[$expectedContent] not found in elements with id '$selector':[\n$elementContents]",
        s"[$expectedContent] element found with id '$selector' and url [$expectedContent]"
      )
    }
  }

  class ClassSelectorWithUrlMatcher(expectedContent: String, selector: String) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val elements: String =
        left.getElementsByClass(selector).attr("href")

      lazy val elementContents = elements.mkString("\t", "\n\t", "")

      MatchResult(
        elements.contains(expectedContent),
        s"[$expectedContent] not found in elements with class '$selector':[\n$elementContents]",
        s"[$expectedContent] element found with class '$selector' and url [$expectedContent]"
      )
    }
  }

  class IdSelectorWithUrlAndTextMatcher(id: String, url: String, text: String) extends Matcher[Document] {
    def apply(left: Document): MatchResult = {
      val element = left.getElementById(id)
      val hrefFound: String = element.attr("href")
      val textFound: String = element.text

      MatchResult(
        hrefFound.contains(url) && textFound.contains(text),
        s"[url:$url][text:$text] not found in element with id:'$id' \nInstead found:[url:$hrefFound][text:$textFound]",
        s"Element found with id '$id' and url [$url] and text [$text]"
      )
    }
  }

  class ElementWithTextMatcher(expectedContent: String) extends Matcher[Element] {
    def apply(left: Element): MatchResult =
      MatchResult(
        left.text == expectedContent,
        s"[$expectedContent] was not equal to [${left.text}]",
        s"[$expectedContent] was equal to [${left.text}]"
      )

  }

  class ElementWithAttributeValueMatcher(expectedContent: String, attribute: String) extends Matcher[Element] {
    def apply(left: Element): MatchResult = {
      val attribVal = left.attr(attribute)
      val attributes = left.attributes().asList().mkString("\t", "\n\t", "")

      MatchResult(
        attribVal == expectedContent,
        s"""[$attribute="$expectedContent"] is not a member of the element's attributes:[\n$attributes]""",
        s"""[$attribute="$expectedContent"] is a member of the element's attributes:[\n$attributes]"""
      )
    }

  }

  class ElementWithClassMatcher(expectedClass: String) extends Matcher[Element] {
    def apply(left: Element): MatchResult = {
      val classes = left.classNames.toList
      val classNames = classes.mkString("\t", "\n\t", "")

      MatchResult(
        classes.contains(expectedClass),
        s"[$expectedClass] is not a member of the element's classes:[\n$classNames]",
        s"[$expectedClass] is a member of the element's classes:[\n$classNames]"
      )
    }
  }

}
