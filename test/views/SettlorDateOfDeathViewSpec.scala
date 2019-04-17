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

import java.time.LocalDate

import controllers.routes
import forms.SettlorDateOfDeathFormProvider
import models.{FullName, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorDateOfDeathView

class SettlorDateOfDeathViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "settlorDateOfDeath"

  val form = new SettlorDateOfDeathFormProvider()()

  "SettlorDateOfDeathView view" must {

    val name = FullName("First", None, "Last")

    val view = viewFor[SettlorDateOfDeathView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, name)(fakeRequest, messages)

    val applyViewF = (form : Form[_]) => applyView(form)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

    behave like pageWithDateFields(form, applyViewF,
      messageKeyPrefix,
      routes.SettlorDateOfDeathController.onPageLoad(NormalMode).url,
      name.toString
    )

    behave like pageWithBackLink(applyView(form))
  }
}
