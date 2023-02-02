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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.ViewUtils

trait OptionStringViewBehaviours extends QuestionViewBehaviours[Option[String]] {

  val valueDefined : Option[String] = Some("answer")
  val valueNotDefined: Option[String] = None

  def optionalStringPage(form: Form[Option[String]],
                        createView: Form[Option[String]] => HtmlFormat.Appendable,
                        sectionKey: Option[String],
                        messageKeyPrefix: String,
                        expectedHintKey: Option[String] = None) = {

    "behave like a page with an optional string value field" when {

      "rendered" must {

        "contain a label for the value" in {

          val doc = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))
          assertContainsLabel(doc, "value", messages(s"$messageKeyPrefix.heading"), expectedHintText)
        }

        "contain an input for the value" in {

          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
        }
      }

      "rendered with a valid form" must {

        "include the form's value in the value input if defined" in {

          val doc = asDocument(createView(form.fill(valueDefined)))
          doc.getElementById("value").attr("value") mustBe valueDefined.value
        }

        "not include the form's value in the value input if not defined" in {
          val doc = asDocument(createView(form.fill(valueNotDefined)))
          doc.getElementById("value").attr("value") mustBe empty
        }

      }

      "rendered with an error" must {

        "show an error summary" in {

          val doc = asDocument(createView(form.withError(error)))
          assertRenderedByClass(doc, "govuk-error-summary")
        }

        "show an error in the value field's label" in {

          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text mustBe s"""${messages(errorPrefix)} ${messages(errorMessage)}"""
        }

        "show an error prefix in the browser title" in {

          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", ViewUtils.breadcrumbTitle(s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title")}""", sectionKey.map(messages(_))))
        }
      }
    }
  }
}
