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

import java.time.LocalDate

import controllers.routes
import forms.SettlorsDateOfBirthFormProvider
import models.{FullName, NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorsDateOfBirthView

class SettlorsDateOfBirthViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "settlorsDateOfBirth"

  val form = new SettlorsDateOfBirthFormProvider()()

  "SettlorsDateOfBirthView view" must {

    val name = FullName("First", None, "Last")

    val view = viewFor[SettlorsDateOfBirthView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, name)(fakeRequest, messages)

    val applyViewF = (form : Form[_]) => applyView(form)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString, "hint")

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(form, applyViewF,
      messageKeyPrefix,
      "value",
      name.toString
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
