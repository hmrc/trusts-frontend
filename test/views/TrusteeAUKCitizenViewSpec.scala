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
import forms.TrusteeAUKCitizenFormProvider
import models.{FullName, NormalMode, UserAnswers}
import pages.TrusteesNamePage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.TrusteeAUKCitizenView

class TrusteeAUKCitizenViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trusteeAUKCitizen"

  val form = new TrusteeAUKCitizenFormProvider()(messageKeyPrefix)

  val index = 0
  val trusteeName = "FirstName LastName"
  val fullName = FullName("FirstName", None, "LastName")


  "trusteeAUKCitizen view" must {

    val userAnswers = emptyUserAnswers
      .set(TrusteesNamePage(index), fullName).success.value

    val view = viewFor[TrusteeAUKCitizenView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, index, trusteeName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, trusteeName)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.TrusteeAUKCitizenController.onSubmit(NormalMode, index).url, None, Seq(fullName.toString))
  }
}