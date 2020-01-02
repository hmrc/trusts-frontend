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

package views

import controllers.register.beneficiaries.routes
import forms.RemoveIndexFormProvider
import models.core.pages.FullName
import pages.register.beneficiaries.individual.IndividualBeneficiaryNamePage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.RemoveIndexView

class RemoveIndexViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "removeIndividualBeneficiary"

  val form = new RemoveIndexFormProvider()(messageKeyPrefix)

  val route = routes.RemoveIndividualBeneficiaryController.onPageLoad(0, fakeDraftId)

  val index = 0

  "RemoveIndex view" must {

    val userAnswers = emptyUserAnswers
      .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value

    val view = viewFor[RemoveIndexView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(messageKeyPrefix, form, index, fakeDraftId, nameOrDescription = "First Last", route)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, "First Last")

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, None, Seq("First Last"))
  }
}
