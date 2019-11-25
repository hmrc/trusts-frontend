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

import forms.AddAssetsFormProvider
import models.NormalMode
import models.registration.pages.{AddAssets, WhatKindOfAsset}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.AddAssetsView

class AddAssetsViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val completeAssets = Seq(
    AddRow("4500", WhatKindOfAsset.Money.toString, "#", "#"),
    AddRow("4500", WhatKindOfAsset.Money.toString, "#", "#")
  )

  val inProgressAssets = Seq(
    AddRow("Tesco", WhatKindOfAsset.Shares.toString, "#", "#")
  )

  val messageKeyPrefix = "addAssets"

  val form = new AddAssetsFormProvider()()

  val view = viewFor[AddAssetsView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, fakeDraftId, Nil, Nil, "Add assets")(fakeRequest, messages)

  def applyView(form: Form[_], inProgressAssets: Seq[AddRow], completeAssets: Seq[AddRow], count : Int): HtmlFormat.Appendable = {
    val title = if (count > 1) s"You have added $count assets" else "Add assets"
    view.apply(form, NormalMode, fakeDraftId, inProgressAssets, completeAssets, title)(fakeRequest, messages)
  }

  "AddAssetsView" when {

    "there are no assets" must {
      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithNoTabularData(applyView(form))

      behave like pageWithBackLink(applyView(form))

      behave like pageWithOptions(form, applyView, AddAssets.options.toSet)
    }

    "there is data in progress" must {

      val viewWithData = applyView(form, inProgressAssets, Nil, 1)

      behave like dynamicTitlePage(viewWithData, "addAssets.singular", "1")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithInProgressTabularData(viewWithData, inProgressAssets)

      behave like pageWithOptions(form, applyView, AddAssets.options.toSet)

    }

    "there is complete data" must {

      val viewWithData = applyView(form, Nil, completeAssets, 2)

      behave like dynamicTitlePage(viewWithData, "addAssets.count", "2")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithCompleteTabularData(viewWithData, completeAssets)

      behave like pageWithOptions(form, applyView, AddAssets.options.toSet)
    }

  }
}
