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

package views.register.trustees.individual

import forms.trustees.TrusteesNameFormProvider
import generators.Generators
import models.NormalMode
import models.core.pages.FullName
import pages.register.trustees.IsThisLeadTrusteePage
import play.api.data.Form
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.trustees.individual.TrusteesNameView

class TrusteesNameViewSpec extends QuestionViewBehaviours[FullName] with Generators {

  val messageKeyPrefix = "trusteesName"
  val leadMessageKeyPrefix = "leadTrusteesName"

  val leadHeading = Messages("leadTrusteesName.heading")
  val heading = Messages("trusteesName.heading")

  val index = 0

  override val form = new TrusteesNameFormProvider()(messageKeyPrefix)

  "LeadTrusteeFullName view" must {

    val userAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), true).success.value

    val view = viewFor[TrusteesNameView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, leadHeading)(fakeRequest, messages)

    behave like normalPage(applyView(form), leadMessageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      leadMessageKeyPrefix,
      Seq(("firstName",None), ("middleName",None), ("lastName",None))
    )

  }

  "TrusteeFullName view" must {

    val userAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), false).success.value

    val view = viewFor[TrusteesNameView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, heading)(fakeRequest, messages)

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