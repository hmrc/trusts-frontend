/*
 * Copyright 2021 HM Revenue & Customs
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

package views.register

import forms.TrustNameFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.MatchingNameView

class MatchingNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trustName"
  val hintKey = "trustName.hint.hasUtr"

  val form = new TrustNameFormProvider()()

  "MatchingNameView view" when {

    "the trust is an existing trust" must {

      val view = viewFor[MatchingNameView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId)(fakeRequest, messages)

      behave like normalPage(applyView(form), None, messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like stringPage(form,
        applyView,
        None,
        messageKeyPrefix,
        Some(hintKey)
      )

      behave like pageWithASubmitButton(applyView(form))
    }
  }
}