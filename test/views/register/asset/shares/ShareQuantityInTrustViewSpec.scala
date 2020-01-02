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

package views.register.asset.shares

import forms.shares.ShareQuantityInTrustFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.asset.shares.ShareQuantityInTrustView

class ShareQuantityInTrustViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "shareQuantityInTrust"

  val form = new ShareQuantityInTrustFormProvider()()

  val index = 0

  val companyName = "Company"

  "ShareQuantityInTrustView view" must {

    val view = viewFor[ShareQuantityInTrustView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, companyName)(fakeRequest, messages)

    behave like pageWithBackLink(applyView(form))

    behave like stringPageWithDynamicTitle(form, applyView, messageKeyPrefix, companyName, Some(messages(s"$messageKeyPrefix.hint")))

    pageWithASubmitButton(applyView(form))
  }
}
