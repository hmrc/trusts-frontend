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

package views.register.trustees.organisation

import forms.trustees.TrusteeBusinessNameFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.trustees.organisation.TrusteeBusinessNameView

class TrusteeBusinessNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trusteeBusinessName"
  val form = new TrusteeBusinessNameFormProvider()()
  val view = viewFor[TrusteeBusinessNameView](Some(emptyUserAnswers))

  "TrusteeBusinessName view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, 0, true)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix)

    behave like pageWithASubmitButton(applyView(form))
  }

  "TrusteeBusinessName view for a UK registered company" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, NormalMode, fakeDraftId, 0, true)(fakeRequest, messages)

      "display hint text" in {
        val doc = asDocument(applyView(form))
        assertContainsText(doc, messages(s"$messageKeyPrefix.hint"))
      }
  }

  "TrusteeBusinessName view for a non-UK registered company" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, 0, false)(fakeRequest, messages)

    "hint text not displayed" in {
      val doc = asDocument(applyView(form))
        assertNotRenderedByClass(doc, "form-hint")
    }
  }

}
