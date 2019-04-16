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
import forms.UKAddressFormProvider
import models.{NormalMode, UKAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorsUKAddressView

class SettlorsUKAddressViewSpec extends QuestionViewBehaviours[UKAddress] {

  val messageKeyPrefix = "settlorsUKAddress"
  val postcodeHintKey = "settlorsUKAddress.postcode.hint"

  override val form = new UKAddressFormProvider()()

  "SettlorsUKAddressView" must {

    val view = viewFor[SettlorsUKAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.SettlorsUKAddressController.onSubmit(NormalMode).url,
      Seq(("line1",None), ("line2",None), ("line3", None), ("townOrCity", None), ("postcode", Some(postcodeHintKey)))
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
