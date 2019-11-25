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

import forms.UKAddressFormProvider
import models.NormalMode
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.UkAddressViewBehaviours
import views.html.IndividualBeneficiaryAddressUKView

class IndividualBeneficiaryAddressUKViewSpec extends UkAddressViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryAddressUK"
  val index = 0
  val name = "First Last"
  val fullName = FullName("First", None, "Last")

  override val form = new UKAddressFormProvider()()

  "IndividualBeneficiaryAddressUKView" must {

    val view = viewFor[IndividualBeneficiaryAddressUKView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, fullName, index)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like ukAddressPage(
      applyView,
      Some(messageKeyPrefix),
      name.toString
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}
