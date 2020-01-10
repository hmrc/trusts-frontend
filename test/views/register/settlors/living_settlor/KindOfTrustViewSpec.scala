/*
 * Copyright 2020 HM Revenue & Customs
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

package views.register.settlors.living_settlor

import forms.KindOfTrustFormProvider
import models.NormalMode
import models.registration.pages.KindOfTrust
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.register.settlors.living_settlor.KindOfTrustView

class KindOfTrustViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "kindOfTrust"

  val form = new KindOfTrustFormProvider()()

  val view = viewFor[KindOfTrustView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

  "KindOfTrustView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithASubmitButton(applyView(form))

  }

  "KindOfTrustView" when {

    "rendered" must {

      "contain radio buttons for the value" in {

        val doc = asDocument(applyView(form))

        for (option <- KindOfTrust.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- KindOfTrust.options) {

      s"rendered with a value of '${option.value}'" must {

        s"have the '${option.value}' radio button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))

          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- KindOfTrust.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
