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

package views.register.asset.business

import controllers.register.asset.business.routes
import forms.InternationalAddressFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.InternationalAddressViewBehaviours
import views.html.register.asset.buisness.BusinessInternationalAddressView

class BusinessInternationalAddressViewSpec extends InternationalAddressViewBehaviours {

  val messageKeyPrefix = "assetInternationalAddress"
  val index = 0
  val buisnessName = "Trust"

  override val form = new InternationalAddressFormProvider()()

  "AssetInternationalAddressView" must {

    val view = viewFor[BusinessInternationalAddressView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, NormalMode, index, fakeDraftId, buisnessName)(fakeRequest, messages)

    behave like pageWithBackLink(applyView(form))

    behave like internationalAddress(
      applyView,
      Some("taskList.assets.label"),
      Some(messageKeyPrefix),
      routes.BusinessInternationalAddressController.onSubmit(NormalMode, index, fakeDraftId).url,
      buisnessName
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
