/*
 * Copyright 2019 HM Revenue & Customs
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

package views.register.asset.shares

import forms.shares.ShareClassFormProvider
import models.NormalMode
import models.registration.pages.ShareClass
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.register.asset.shares.ShareClassView

class ShareClassViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "shareClass"

  val form = new ShareClassFormProvider()()

  val view = viewFor[ShareClassView](Some(emptyUserAnswers))

  val index = 0

  val companyName = "Company"

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, fakeDraftId, index, companyName)(fakeRequest, messages)

  "ShareClassView" must {

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, companyName)

    behave like pageWithBackLink(applyView(form))

    pageWithASubmitButton(applyView(form))
  }

  "ShareClassView" when {

    "rendered" must {

      "contain radio buttons for the value" in {

        val doc = asDocument(applyView(form))

        for (option <- ShareClass.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- ShareClass.options) {

      s"rendered with a value of '${option.value}'" must {

        s"have the '${option.value}' radio button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))

          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- ShareClass.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
