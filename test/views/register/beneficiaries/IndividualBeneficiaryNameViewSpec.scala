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

package views.register.beneficiaries

import forms.IndividualBeneficiaryNameFormProvider
import generators.Generators
import models.NormalMode
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.beneficiaries.IndividualBeneficiaryNameView

class IndividualBeneficiaryNameViewSpec extends QuestionViewBehaviours[FullName] with Generators {

  val messageKeyPrefix = "individualBeneficiaryName"
  val index = 0


  override val form = new IndividualBeneficiaryNameFormProvider()()

  "IndividualBeneficiaryNameView" must {

    val view = viewFor[IndividualBeneficiaryNameView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId,index)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      Seq(("firstName",None), ("middleName",None), ("lastName",None))
    )
  }
}
