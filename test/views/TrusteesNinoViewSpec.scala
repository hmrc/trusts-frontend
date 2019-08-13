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
import forms.TrusteesNinoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.TrusteesNinoView

class TrusteesNinoViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trusteesNino"
  val index = 0
  val trusteeName = "FirstName LastName"
  val hintKey = "trusteesNino.hint"

  val form = new TrusteesNinoFormProvider()(messageKeyPrefix)

  "TrusteesNinoView view" must {

    val view = viewFor[TrusteesNinoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, trusteeName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, trusteeName, "hint")

    behave like pageWithBackLink(applyView(form))

    behave like stringPageWithDynamicTitle(form, applyView, messageKeyPrefix, trusteeName, Some(hintKey))

    behave like pageWithASubmitButton(applyView(form))

  }
}
