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

package views.register.settlors.living_settlor.business

import forms.UtrFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.settlors.living_settlor.business.SettlorBusinessUtrView

class SettlorBusinessUtrViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "settlorBusinessUtr"
  val index = 0
  val settlorBusinessBusinessName = "BusinessName"
  val hintKey = "settlorBusinessUtr.hint"

  val form = new UtrFormProvider()(messageKeyPrefix)

  "SettlorBusinessUtrView view" must {

    val view = viewFor[SettlorBusinessUtrView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, settlorBusinessBusinessName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, settlorBusinessBusinessName, "hint")

    behave like stringPageWithDynamicTitle(form, applyView,
      messageKeyPrefix,
      settlorBusinessBusinessName,
      Some(hintKey)
    )

    behave like pageWithBackLink(applyView(form))
  }
}
