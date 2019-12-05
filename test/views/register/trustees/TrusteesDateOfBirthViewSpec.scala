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

package views.register.trustees

import java.time.LocalDate

import forms.trustees.TrusteesDateOfBirthFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.trustees.TrusteesDateOfBirthView

class TrusteesDateOfBirthViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "trusteesDateOfBirth"
  val index = 0
  val trusteeName = "FirstName LastName"

  val form = new TrusteesDateOfBirthFormProvider()()

  "TrusteesDateOfBirthView view" must {

    val view = viewFor[TrusteesDateOfBirthView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, messageKeyPrefix, trusteeName)(fakeRequest, messages)

    val applyViewF = (form : Form[_]) => applyView(form)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, trusteeName, "hint")

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(form, applyViewF,
      messageKeyPrefix,
      "value",
      trusteeName
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
