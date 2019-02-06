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
import models.{AddATrustee, FullName, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.AddATrusteeView

class AddATrusteeViewSpec extends OptionsViewBehaviours {

  val messageKeyPrefix = "addATrustee"

  val form = new AddATrusteeFormProvider()()

  val view = viewFor[AddATrusteeView](Some(emptyUserAnswers))

  val trustee = Some(FullName("Trustee", None, "Trustee"))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, trustee)(fakeRequest, messages)

  "AddATrusteeView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

//    behave like pageWithTabularData()

    behave like pageWithOptions(form, applyView, AddATrustee.options)
  }
}
