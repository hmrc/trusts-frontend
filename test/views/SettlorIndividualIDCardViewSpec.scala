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
import forms.PassportIdCardFormProvider
import models.{FullName, NormalMode, PassportIdCardDetails}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorIndividualIDCardView

class SettlorIndividualIDCardViewSpec extends QuestionViewBehaviours[PassportIdCardDetails] {

  val messageKeyPrefix = "settlorIndividualIDCard"
  val index = 0
  val name = FullName("First", Some("Middle"), "Last")

  override val form = new PassportIdCardFormProvider()()

  "SettlorIndividualIDCardView" must {

    val view = viewFor[SettlorIndividualIDCardView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, NormalMode, fakeDraftId, index, name)(fakeRequest, messages)


    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      Seq(("field1", None), ("field2", None))
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
