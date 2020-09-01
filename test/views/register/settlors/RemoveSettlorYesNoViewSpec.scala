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

package views.register.settlors

import controllers.register.settlors.routes
import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.settlors.RemoveSettlorYesNoView

class RemoveSettlorYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlors.removeYesNo"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  val settlorLabel = "Label"

  "RemoveSettlorYesNoView" when {

    "deceased settlor" must {

      val view = viewFor[RemoveSettlorYesNoView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(
          form,
          fakeDraftId,
          routes.RemoveSettlorYesNoController.onSubmitDeceased(fakeDraftId),
          settlorLabel
        )(fakeRequest, messages)

      behave like dynamicTitlePage(applyView(form), Some("taskList.settlors.label"), messageKeyPrefix, settlorLabel)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, Some("taskList.settlors.label"), messageKeyPrefix, None, Seq(settlorLabel))

      behave like pageWithASubmitButton(applyView(form))
    }

    "living settlor" must {

      val index: Int = 0

      val view = viewFor[RemoveSettlorYesNoView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(
          form,
          fakeDraftId,
          routes.RemoveSettlorYesNoController.onSubmitLiving(index, fakeDraftId),
          settlorLabel
        )(fakeRequest, messages)

      behave like dynamicTitlePage(applyView(form), Some("taskList.settlors.label"), messageKeyPrefix, settlorLabel)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, Some("taskList.settlors.label"), messageKeyPrefix, None, Seq(settlorLabel))

      behave like pageWithASubmitButton(applyView(form))
    }
  }
}
