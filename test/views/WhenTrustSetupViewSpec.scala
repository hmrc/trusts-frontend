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
import forms.WhenTrustSetupFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.WhenTrustSetupView

class WhenTrustSetupViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "whenTrustSetup"

  val form = new WhenTrustSetupFormProvider()()

  "WhenTrustSetupView view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[WhenTrustSetupView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    val applyViewF = (form : Form[_]) => applyView(form)

    behave like normalPage(applyView(form), messageKeyPrefix,s"hint")

    behave like pageWithTextFields(form, applyViewF, messageKeyPrefix, routes.GovernedInsideTheUKController.onPageLoad(NormalMode).url)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithASubmitButton(applyView(form))
  }
}
