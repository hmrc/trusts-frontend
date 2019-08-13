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

import forms.property_or_land.PropertyOrLandAddressUkYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.property_or_land.PropertyOrLandAddressUkYesNoView

class PropertyOrLandAddressUkYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "propertyOrLandAddressUkYesNo"

  val form = new PropertyOrLandAddressUkYesNoFormProvider()()

  val index = 1

  "PropertyOrLandAddressUkYesNo view" must {

    val view = viewFor[PropertyOrLandAddressUkYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix)
  }
}
