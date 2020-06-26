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

package views.register.asset.other

import forms.ValueFormProvider
import models.NormalMode
import models.core.UserAnswers
import pages.register.asset.other.OtherAssetDescriptionPage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.asset.other.OtherAssetValueView

class OtherAssetValueViewSpec extends StringViewBehaviours {

  val prefix = "assets.other.value"
  val hintKey = s"$prefix.hint"

  val form: Form[String] = new ValueFormProvider().withPrefix(prefix)
  val index = 0
  val description: String = "Description"

  "OtherAssetValueView" must {

    val userAnswers: UserAnswers = emptyUserAnswers.set(OtherAssetDescriptionPage(index), description).success.value

    val view = viewFor[OtherAssetValueView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, description)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), prefix, description)

    behave like pageWithBackLink(applyView(form))

    behave like stringPageWithDynamicTitle(form, applyView, prefix, description, Some(hintKey))

    behave like pageWithASubmitButton(applyView(form))

  }
}
