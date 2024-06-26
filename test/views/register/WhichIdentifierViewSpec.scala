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

package views.register

import forms.WhichIdentifierFormProvider
import models.WhichIdentifier
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.register.WhichIdentifierView

class WhichIdentifierViewSpec extends OptionsViewBehaviours {

  val messageKeyPrefix = "whichIdentifier"

  val form = new WhichIdentifierFormProvider()()

  val view: WhichIdentifierView = viewFor[WhichIdentifierView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form)(fakeRequest, messages)

  "WhichIdentifierView" must {

    behave like normalPage(applyView(form), None, messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithASubmitButton(applyView(form))

    behave like pageWithOptionsWithHints(form, applyView, WhichIdentifier.options)
  }
}
