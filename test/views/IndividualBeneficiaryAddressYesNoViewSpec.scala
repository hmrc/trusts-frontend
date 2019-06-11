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
import forms.IndividualBeneficiaryAddressYesNoFormProvider
import models.{FullName, NormalMode, UserAnswers}
import pages.IndividualBeneficiaryNamePage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IndividualBeneficiaryAddressYesNoView

class IndividualBeneficiaryAddressYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryAddressYesNo"
  val index = 0
  val name = "First Last"
  val fullName = FullName("First", None, "Last")

  val form = new IndividualBeneficiaryAddressYesNoFormProvider()()

  "IndividualBeneficiaryAddressYesNo view" must {

    val userAnswers = emptyUserAnswers
      .set(IndividualBeneficiaryNamePage(index), fullName).success.value

    val view = viewFor[IndividualBeneficiaryAddressYesNoView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fullName, index)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IndividualBeneficiaryAddressYesNoController.onSubmit(NormalMode, index).url, None, Seq(fullName.toString))

    behave like pageWithASubmitButton(applyView(form))
  }
}
