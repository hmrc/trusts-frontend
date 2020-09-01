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

import forms.YesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.settlors.living_settlor.EmployerFinancedRbsYesNoView

class EmployerFinancedRbsYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "employerFinancedRbsYesNo"

  val form = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "EmployerFinancedRbsYesNo view" must {

    val view = viewFor[EmployerFinancedRbsYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), Some("taskList.settlors.label"), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, Some("taskList.settlors.label"), messageKeyPrefix, None)

    behave like pageWithASubmitButton(applyView(form))
  }
}
