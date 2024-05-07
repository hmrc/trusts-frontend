/*
 * Copyright 2024 HM Revenue & Customs
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

import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.suitability.TaxLiabilityInCurrentTaxYearYesNoView

class TaxLiabilityInCurrentTaxYearYesNoViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix = "suitability.taxLiabilityInCurrentTaxYear"

  override val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  private val currentTaxYearStartAndEnd: (String, String) = ("6 April 2018", "5 April 2019")

  "TaxLiabilityInCurrentTaxYearYesNo View" must {

    val view = viewFor[TaxLiabilityInCurrentTaxYearYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, currentTaxYearStartAndEnd)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), None, messageKeyPrefix, Seq(currentTaxYearStartAndEnd._1, currentTaxYearStartAndEnd._2))

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, None, messageKeyPrefix, None, Seq(currentTaxYearStartAndEnd._1, currentTaxYearStartAndEnd._2))

    behave like pageWithASubmitButton(applyView(form))
  }
}
