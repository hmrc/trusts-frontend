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

package views.register.agents

import forms.UKAddressFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.UkAddressViewBehaviours
import views.html.register.agents.AgentUKAddressView

class AgentUKAddressViewSpec extends UkAddressViewBehaviours {

  val messageKeyPrefix = "site.address.uk"

  val agencyName = "Hadrian"

  override val form = new UKAddressFormProvider()()

  "AgentUKAddressView" must {

    val view = viewFor[AgentUKAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, agencyName)(fakeRequest, messages)


    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, agencyName)

    behave like pageWithBackLink(applyView(form))

    behave like ukAddressPage(
      applyView,
      Some(messageKeyPrefix),
      agencyName
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}

