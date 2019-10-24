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
import models.NormalMode
import views.behaviours.ViewBehaviours
import views.html.{InformationMaintainingThisTrustView, SettlorInfoView}
import play.api.data.Form
import views.html.helper.form

class InformationMaintainingThisTrustViewSpec extends ViewBehaviours {

  "SettlorInfo view" must {

    val utr = "1234545678"

    val view = viewFor[InformationMaintainingThisTrustView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId, utr)(fakeRequest, messages)

    behave like normalPage(applyView, "informationMaintainingThisTrust",
      "subHeading",
      "warning",
      "h3",
      "paragraph1",
      "list1",
      "list2",
      "printsave.link",
      "list3",
      "list4",
      "h2",
      "paragraph2",
      "paragraph3"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithASubmitButton(applyView)
  }
}
