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

import forms.AddATrusteeFormProvider
import models.{AddATrustee, NormalMode, TrusteeOrIndividual}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.TrusteeRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.AddATrusteeView

class AddATrusteeViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val completeTrustees = Seq(
    TrusteeRow("trustee one", TrusteeOrIndividual.Individual, "#", "#"),
    TrusteeRow("trustee two", TrusteeOrIndividual.Individual, "#", "#"),
    TrusteeRow("trustee three", TrusteeOrIndividual.Individual, "#", "#")
  )

  val inProgressTrustees = Seq(
    TrusteeRow("trustee one", TrusteeOrIndividual.Individual, "#", "#"),
    TrusteeRow("trustee two", TrusteeOrIndividual.Individual, "#", "#"),
    TrusteeRow("trustee three", TrusteeOrIndividual.Individual, "#", "#")
  )

  val messageKeyPrefix = "addATrustee"

  val form = new AddATrusteeFormProvider()()

  val view = viewFor[AddATrusteeView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, Nil, Nil)(fakeRequest, messages)

  def applyView(form: Form[_], inProgressTrustees: Seq[TrusteeRow], completeTrustees: Seq[TrusteeRow]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, inProgressTrustees, completeTrustees)(fakeRequest, messages)

  "AddATrusteeView" when {

    "there is no trustee data" must {

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithNoTabularData(applyView(form))

      behave like pageWithOptions(form, applyView, AddATrustee.options)
    }


    "there is data in progress" must {

      val viewWithData = applyView(form, inProgressTrustees, Nil)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithInProgressTabularData(viewWithData, inProgressTrustees)

      behave like pageWithOptions(form, applyView, AddATrustee.options)
    }

    "there is complete data" must {

      val viewWithData = applyView(form, Nil, completeTrustees)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithCompleteTabularData(viewWithData, completeTrustees)

      behave like pageWithOptions(form, applyView, AddATrustee.options)
    }


    "there is both in progress and complete data" must {

      val viewWithData = applyView(form, inProgressTrustees, completeTrustees)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithTabularData(viewWithData, inProgressTrustees, completeTrustees)

      behave like pageWithOptions(form, applyView, AddATrustee.options)
    }

  }
}