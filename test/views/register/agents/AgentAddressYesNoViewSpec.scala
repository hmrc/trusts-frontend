/*
 * Copyright 2021 HM Revenue & Customs
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

package views.register.agents

import forms.YesNoFormProvider
import models.NormalMode
import pages.register.agents.AgentNamePage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.agents.AgentAddressYesNoView

class AgentAddressYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "agentAddressYesNo"

  val name = "First Last"

  val form = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "AgentAddressYesNo view" must {

    val userAnswers = emptyUserAnswers
      .set(AgentNamePage, name).success.value

    val view = viewFor[AgentAddressYesNoView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), None, messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, None, messageKeyPrefix, None, Seq(name))

    behave like pageWithASubmitButton(applyView(form))
  }
}
