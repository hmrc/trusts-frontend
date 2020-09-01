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

package views.register.suitability

import views.behaviours.ViewBehaviours
import views.html.register.suitability.BeforeYouContinueView

class BeforeYouContinueViewSpec extends ViewBehaviours {

  "BeforeYouContinue View" must {

    val application = applicationBuilder().build()

    val view = application.injector.instanceOf[BeforeYouContinueView]

    val applyView = view.apply(fakeDraftId)(fakeRequest, messages)

    behave like normalPage(
      applyView,
      None,
      "suitability.beforeYouContinue",
      "p1", "bullet1", "bullet2", "p2", "link1", "link2"
    )

    behave like pageWithASubmitButton(applyView)
  }
}
