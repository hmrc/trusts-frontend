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

package views.register

import play.api.Application
import views.behaviours.ViewBehaviours
import views.html.register.TrustAlreadyRegisteredView

class TrustAlreadyRegisteredViewSpec extends ViewBehaviours {

  private val application: Application = applicationBuilder().build()

  private val view: TrustAlreadyRegisteredView = application.injector.instanceOf[TrustAlreadyRegisteredView]

  private val fakeUtr: String = "utr"

  "TrustAlreadyRegistered View" when {

    "agent user" must {

      val applyView = view.apply(fakeDraftId, fakeUtr, isAgent = true)(fakeRequest, messages)

      behave like pageWithSubHeading(applyView, s"This trust’s UTR: $fakeUtr")

      behave like normalPage(
        applyView,
        "trustAlreadyRegistered",
        "agent.p1", "agent.p1.link"
      )

      behave like pageWithASubmitButton(applyView)
    }

    "non-agent user" must {

      val applyView = view.apply(fakeDraftId, fakeUtr, isAgent = false)(fakeRequest, messages)

      behave like pageWithSubHeading(applyView, s"This trust’s UTR: $fakeUtr")

      behave like normalPage(
        applyView,
        "trustAlreadyRegistered",
        "p1", "bullet1", "bullet2", "p2", "p2.link"
      )

      behave like pageWithASubmitButton(applyView)
    }
  }
}
