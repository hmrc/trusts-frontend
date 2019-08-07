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

package views.property_or_land

import controllers.property_or_land.routes
import forms.UKAddressFormProvider
import models.{NormalMode, UKAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.property_or_land.WhatIsThePropertyOrLandUKAddressView

class WhatIsThePropertyOrLandUKAddressViewSpec extends QuestionViewBehaviours[UKAddress] {

  val propertyOrLandPrefix = "whatIsThePropertyOrLandUKAddress"
  val genericAddressPrefix = "site.address.uk"
  val postCodeHintKey = "site.address.uk.postcode.hint"
  val index = 0

  override val form = new UKAddressFormProvider()()

  "WhatIsThePropertyOrLandUKAddressView view" must {

    val view = viewFor[WhatIsThePropertyOrLandUKAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), genericAddressPrefix, "the property or land")

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      genericAddressPrefix,
      routes.WhatIsThePropertyOrLandUKAddressController.onSubmit(NormalMode, index, fakeDraftId).url,
      Seq(("line1", None), ("line2", None), ("line3", None), ("townOrCity", None), ("postcode", Some(postCodeHintKey))),
      "the property or land"
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}
