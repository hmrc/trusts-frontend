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

package views.register.asset

import forms.YesNoFormProvider
import models.NormalMode
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.asset.RemoveAssetYesNoView
import views.html.register.settlors.deceased_settlor.SettlorsNINoYesNoView

class RemoveAssetYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "assets.removeYesNo"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "RemoveAssetYesNoView" must {

    val assetLabel = "Label"
    val index: Int = 0

    val view = viewFor[RemoveAssetYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, assetLabel)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, assetLabel)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, None, Seq(assetLabel))

    behave like pageWithASubmitButton(applyView(form))
  }
}
