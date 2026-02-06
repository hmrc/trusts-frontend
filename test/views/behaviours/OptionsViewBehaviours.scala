/*
 * Copyright 2026 HM Revenue & Customs
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

import models.WhichIdentifier
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioOption

trait OptionsViewBehaviours extends ViewBehaviours {

  def pageWithOptions[T](form: Form[T], applyView: Form[T] => HtmlFormat.Appendable, options: List[RadioOption]): Unit =

    "behave like a page with radio options" when {

      "rendered" must {

        "contain radio buttons for the values" in {

          val doc = asDocument(applyView(form))

          for (option <- options)
            assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      for (option <- options)

        s"rendered with a value of '${option.value}'" must {

          s"have the '${option.value}' radio button selected" in {

            val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))

            assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

            for (unselectedOption <- options.filterNot(o => o == option))
              assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
          }
        }
    }

  def pageWithOptionsWithHints[T](
    form: Form[T],
    applyView: Form[T] => HtmlFormat.Appendable,
    options: List[(RadioOption, String)]
  ): Unit =

    "behave like a page with radio options" when {

      "rendered" must {

        "contain radio buttons for the values" in {

          val doc = asDocument(applyView(form))

          for (option <- options) {
            assertContainsRadioButton(doc, option._1.id, "value", option._1.value, isChecked = false)
            if (option._2.nonEmpty) assertRadioButtonContainsHint(doc, option._1.id + "-item-hint", messages(option._2))
          }
        }
      }

      for (option <- options)

        s"rendered with a value of '${option._1.value}'" must {

          s"have the '${option._1.value}' radio button selected" in {

            val doc = asDocument(applyView(form.bind(Map("value" -> s"${option._1.value}"))))

            assertContainsRadioButton(doc, option._1.id, "value", option._1.value, isChecked = true)
            if (option._2.nonEmpty) assertRadioButtonContainsHint(doc, option._1.id + "-item-hint", messages(option._2))

            for (unselectedOption <- WhichIdentifier.options.filterNot(o => o == option)) {
              assertContainsRadioButton(
                doc,
                unselectedOption._1.id,
                "value",
                unselectedOption._1.value,
                isChecked = false
              )
              if (unselectedOption._2.nonEmpty)
                assertRadioButtonContainsHint(doc, unselectedOption._1.id + "-item-hint", messages(unselectedOption._2))
            }
          }
        }
    }

}
