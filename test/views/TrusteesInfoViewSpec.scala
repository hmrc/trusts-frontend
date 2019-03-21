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

import models.NormalMode
import views.behaviours.ViewBehaviours
import views.html.TrusteesInfoView
import controllers._


class TrusteesInfoViewSpec extends ViewBehaviours {

  val url = routes.TrustRegisteredOnlineController.onPageLoad(NormalMode).url

  "TrusteesInfo view" must {

    val view = viewFor[TrusteesInfoView](Some(emptyUserAnswers))

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "trusteesInfo",
    "subheading1",
      "paragraph1",
      "paragraph2",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "paragraph3",
      "subheading2",
      "paragraph4",
      "bulletpoint4",
      "bulletpoint5",
      "bulletpoint6",
      "paragraph6",
      "warning"
    )

    behave like pageWithBackLink(applyView)

  }
}
