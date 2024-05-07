/*
 * Copyright 2024 HM Revenue & Customs
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

package views.register.suitability

import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.IsExpressYesNoViewBehaviours
import views.html.register.suitability.ExpressTrustYesNoView

class ExpressTrustYesNoViewSpec extends IsExpressYesNoViewBehaviours {

  val prefix = "suitability.expressTrust"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(prefix)

  "ExpressTrustYesNo view" must {

    val view = viewFor[ExpressTrustYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form)(fakeRequest, messages)

    behave like normalPage(applyView(form), None, prefix,
      "subheading1", "p1", "p2", "p3", "bullet1", "bullet2", "subheading2", "p4", "subheading3")

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, None, prefix)

    behave like pageWithASubmitButton(applyView(form))
  }
}
