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
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.TrusteeAUKCitizenView

class TrusteeAUKCitizenViewSpec extends YesNoViewBehaviours {

  val name = FullName("First Name", Some(""), "Last Name")

  val messageKeyPrefix = "trusteeAUKCitizen"

  val heading = Messages("trusteeAUKCitizen.heading", name)

  val form = new TrusteeAUKCitizenFormProvider()()

  val index = 0

  "trusteeAUKCitizen view" must {

    val userAnswers = UserAnswers(userAnswersId)
      .set(TrusteesNamePage(index), name).success.value

    val application = applicationBuilder(Some(userAnswers)).build()

    val view = application.injector.instanceOf[TrusteeAUKCitizenView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, index, heading)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix, Nil, Seq(name.toString))

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.TrusteeAUKCitizenController.onSubmit(NormalMode, index).url)
  }
}
