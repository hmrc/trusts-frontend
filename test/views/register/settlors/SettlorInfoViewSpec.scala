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

import controllers.register.settlors.deceased_settlor.routes
import models.NormalMode
import views.behaviours.ViewBehaviours
import views.html.register.settlors.SettlorInfoView

class SettlorInfoViewSpec extends ViewBehaviours {

  "SettlorInfo view" must {

    val view = viewFor[SettlorInfoView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView, "settlorInfo",
      "caption",
      "subheading1",
      "paragraph1",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "bulletpoint4",
      "paragraph2",
      "bulletpoint5",
      "bulletpoint6",
      "bulletpoint7",
      "bulletpoint8",
      "subheading2",
      "paragraph3",
      "bulletpoint9",
      "bulletpoint10",
      "subheading3",
      "paragraph4",
      "paragraph5",
      "bulletpoint11",
      "bulletpoint12",
      "bulletpoint13"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithContinueButton(applyView,routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, fakeDraftId).url )
  }
}
