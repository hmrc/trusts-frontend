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

package views.register.asset.property_or_land

import controllers.register.asset.property_or_land.routes
import forms.UKAddressFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.UkAddressViewBehaviours
import views.html.register.asset.property_or_land.PropertyOrLandUKAddressView

class PropertyOrLandUKAddressViewSpec extends UkAddressViewBehaviours {

  val messageKeyPrefix = "site.address.uk"
  val entityName = "the property or land"
  val index = 0

  override val form = new UKAddressFormProvider()()

  "PropertyOrLandUKAddressView view" must {

    val view = viewFor[PropertyOrLandUKAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), Some("taskList.assets.label"), messageKeyPrefix, entityName)

    behave like pageWithBackLink(applyView(form))

    behave like ukAddressPage(
      applyView,
      Some("taskList.assets.label"),
      Some(messageKeyPrefix),
      routes.PropertyOrLandUKAddressController.onSubmit(NormalMode, index, fakeDraftId).url,
      entityName
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
