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

package views

import forms.WhatTypeOfBeneficiaryFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.WhatTypeOfBeneficiaryView

class WhatTypeOfBeneficiaryViewSpec extends ViewBehaviours {

  val messageKeyPrefixFirst = "whatTypeOfBeneficiary.first"
  val messageKeyPrefixNext = "whatTypeOfBeneficiary.next"

  val form = new WhatTypeOfBeneficiaryFormProvider()()

  val view = viewFor[WhatTypeOfBeneficiaryView](Some(emptyUserAnswers))

  def applyView(form: Form[_], isAdded :Boolean): HtmlFormat.Appendable =
    view.apply(form, NormalMode, fakeDraftId,isAdded)(fakeRequest, messages)

  "WhatTypeOfBeneficiaryView" must {

    "when no beneficiaries have been added" must {
      behave like normalPage(applyView(form,false), messageKeyPrefixFirst)
    }

    "when beneficiaries has been added" must {
      behave like normalPage(applyView(form, true), messageKeyPrefixNext)
    }

    behave like pageWithBackLink(applyView(form,false))
  }

  "WhatTypeOfBeneficiaryView" when {

    "rendered" must {

      "contain radio buttons for the value" in {

        val doc = asDocument(applyView(form,false))

        for (option <- WhatTypeOfBeneficiary.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- WhatTypeOfBeneficiary.options) {

      s"rendered with a value of '${option.value}'" must {

        s"have the '${option.value}' radio button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}")), false))

          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- WhatTypeOfBeneficiary.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
