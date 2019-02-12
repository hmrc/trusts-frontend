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

import controllers.routes
import forms.TrusteesNameFormProvider
import generators.Generators
import models.{FullName, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.TrusteesNameView

class TrusteesNameViewSpec extends QuestionViewBehaviours[FullName] with Generators {

  val messageKeyPrefix = "trusteesName"
  val applicantName: String = nonEmptyString.sample.value

  val index = 0

  override val form = new TrusteesNameFormProvider()()

  "TrusteeFullName view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
    val view = application.injector.instanceOf[TrusteesNameView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, index)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.TrusteesNameController.onSubmit(NormalMode, index).url,
      "firstName", "middleName", "lastName"
    )

  }
}