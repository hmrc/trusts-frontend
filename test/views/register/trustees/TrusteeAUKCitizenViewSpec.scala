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

package views.register.trustees

import forms.YesNoFormProvider
import models.NormalMode
import models.core.pages.FullName
import pages.trustees.TrusteesNamePage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.trustees.TrusteeAUKCitizenView

class TrusteeAUKCitizenViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trusteeAUKCitizen"

  val form = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  val index = 0
  val trusteeName = "FirstName LastName"
  val fullName = FullName("FirstName", None, "LastName")


  "trusteeAUKCitizen view" must {

    val userAnswers = emptyUserAnswers
      .set(TrusteesNamePage(index), fullName).success.value

    val view = viewFor[TrusteeAUKCitizenView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, messageKeyPrefix, trusteeName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, trusteeName)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, None, Seq(fullName.toString))
  }
}