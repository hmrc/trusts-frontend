/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.Application
import views.behaviours.ViewBehaviours
import views.html.register.suitability.NoNeedToRegisterView

class NoNeedToRegisterViewSpec extends ViewBehaviours {

  private val application: Application = applicationBuilder().build()

  private val view: NoNeedToRegisterView = application.injector.instanceOf[NoNeedToRegisterView]

  "BeforeYouContinue View" when {

    "agent user" must {

      val applyView = view.apply(isAgent = true)(fakeRequest, messages)

      behave like normalPage(
        applyView,
        None,
        "suitability.noNeedToRegister",
        "p1", "p2", "p2.link", "p3", "p3.link", "p4", "p4.link"
      )
    }

    "non-agent user" must {

      val applyView = view.apply(isAgent = false)(fakeRequest, messages)

      behave like normalPage(
        applyView,
        None,
        "suitability.noNeedToRegister",
        "p1", "p2", "p2.link", "p3", "p3.link"
      )
    }
  }
}
