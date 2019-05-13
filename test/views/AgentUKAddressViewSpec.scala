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
import forms.AgentUKAddressFormProvider
import models.{NormalMode, AgentUKAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.AgentUKAddressView

class AgentUKAddressViewSpec extends QuestionViewBehaviours[AgentUKAddress] {

  val messageKeyPrefix = "agentUKAddress"

  override val form = new AgentUKAddressFormProvider()()

  "AgentUKAddressView" must {

    val view = viewFor[AgentUKAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.AgentUKAddressController.onSubmit(NormalMode).url,
      Seq(("field1", None), ("field2", None))
    )
  }
}
