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

import forms.AddABeneficiaryFormProvider
import models.{AddABeneficiary, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.AddABeneficiaryView

class AddABeneficiaryViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val completeBeneficiaries = Seq(
    AddRow("beneficiary one", "Individual Beneficiary", "#", "#"),
    AddRow("beneficiary two", "Individual Beneficiary", "#", "#"),
    AddRow("beneficiary three", "Individual Beneficiary", "#", "#"),
    AddRow("class of beneficiary", "Class of beneficiaries", "#", "#")
  )

  val inProgressBeneficiaries = Seq(
    AddRow("beneficiary four", "Individual Beneficiary", "#", "#"),
    AddRow("beneficiary five", "Individual Beneficiary", "#", "#"),
    AddRow("beneficiary six", "Individual Beneficiary", "#", "#"),
    AddRow("class of beneficiary 2", "Class of beneficiaries", "#", "#")
  )
  val messageKeyPrefix = "addABeneficiary"

  val form = new AddABeneficiaryFormProvider()()

  val view = viewFor[AddABeneficiaryView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, fakeDraftId, Nil, Nil, "Add a beneficiary")(fakeRequest, messages)

  def applyView(form: Form[_], inProgressBeneficiaries: Seq[AddRow], completeBeneficiaries: Seq[AddRow], count : Int): HtmlFormat.Appendable = {
    val title = if (count > 1) s"You have added $count beneficiaries" else "You have added 1 beneficiary"
    view.apply(form, NormalMode, fakeDraftId, inProgressBeneficiaries, completeBeneficiaries, title)(fakeRequest, messages)
  }


  "AddABeneficiaryView" when {

    "there is no beneficiary data" must {

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithNoTabularData(applyView(form))

      behave like pageWithOptions(form, applyView, AddABeneficiary.options.toSet)
    }

    "there is data in progress" must {

      val viewWithData = applyView(form, inProgressBeneficiaries, Nil, 4)

      behave like dynamicTitlePage(viewWithData, "addABeneficiary.count", "4")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithInProgressTabularData(viewWithData, inProgressBeneficiaries)

      behave like pageWithOptions(form, applyView, AddABeneficiary.options.toSet)
    }

    "there is complete data" must {

      val viewWithData = applyView(form, Nil, completeBeneficiaries, 4)

      behave like dynamicTitlePage(viewWithData, "addABeneficiary.count", "4")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithCompleteTabularData(viewWithData, completeBeneficiaries)

      behave like pageWithOptions(form, applyView, AddABeneficiary.options.toSet)
    }

    "there is both in progress and complete data" must {

      val viewWithData = applyView(form, inProgressBeneficiaries, completeBeneficiaries, 8)

      behave like dynamicTitlePage(viewWithData, "addABeneficiary.count", "8")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithTabularData(viewWithData, inProgressBeneficiaries, completeBeneficiaries)

      behave like pageWithOptions(form, applyView, AddABeneficiary.options.toSet)
    }
  }

}
