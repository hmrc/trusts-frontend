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

import java.time.LocalDate

import forms.{DateOfBirthFormProvider, EfrbsStartDateFormProvider}
import models.NormalMode
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.settlors.living_settlor.{EmployerFinancedRbsStartDateView, SettlorIndividualDateOfBirthView}

class EmployerFinancedRbsStartDateViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "employerFinancedRbsStartDate"

  val form = new EfrbsStartDateFormProvider(fakeFrontendAppConfig)()

  "EmployerFinancedRbsStartDate view" must {

    val view = viewFor[EmployerFinancedRbsStartDateView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    val applyViewF = (form : Form[_]) => applyView(form)

    behave like normalPage(applyView(form), Some("taskList.settlors.label"), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(form, applyViewF,
      Some("taskList.settlors.label"),
      messageKeyPrefix,
      "value"
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
