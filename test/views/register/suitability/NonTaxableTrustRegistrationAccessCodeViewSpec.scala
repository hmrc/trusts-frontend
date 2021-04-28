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

package views.register.suitability

import forms.AccessCodeFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.suitability.NonTaxableTrustRegistrationAccessCodeView

class NonTaxableTrustRegistrationAccessCodeViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "nonTaxableTrustRegistrationAccessCode"

  val form = new AccessCodeFormProvider()()

  "NonTaxableTrustRegistrationAccessCodeView" must {

    val view = viewFor[NonTaxableTrustRegistrationAccessCodeView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId)(fakeRequest, messages)

    behave like normalPageTitleWithCaption(
      applyView(form),
      messageKeyPrefix,
      messageKeyPrefix,
      "p1", "locked", "subHeading", "p2", "p3", "p4",
      "no-access", "no-access.p1", "no-access.p2",
      "no-access.bulletpoint1", "no-access.bulletpoint2", "no-access.bulletpoint3"
    )

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, None, messageKeyPrefix)

    behave like pageWithHint(applyView(form), s"$messageKeyPrefix.hint")

    behave like pageWithText(applyView(form), fakeFrontendAppConfig.trustsEmail)

    behave like pageWithASubmitButton(applyView(form))
  }
}