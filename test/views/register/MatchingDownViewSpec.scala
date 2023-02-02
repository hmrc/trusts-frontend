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

package views.register

import play.api.Application
import views.behaviours.ViewBehaviours
import views.html.register.MatchingDownView

class MatchingDownViewSpec extends ViewBehaviours {

  private val application: Application = applicationBuilder().build()

  private val view: MatchingDownView = application.injector.instanceOf[MatchingDownView]

  "MatchingDown View" when {

    "agent user" must {

      val applyView = view.apply(isAgent = true)(fakeRequest, messages)

      behave like normalPage(
        applyView,
        None,
        "matchingDown",
        "p1", "p2.beforeLink", "p2.link", "p2.afterLink", "p3", "p3.link"
      )

      behave like pageWithASignOutButton(applyView)
    }

    "non-agent user" must {

      val applyView = view.apply(isAgent = false)(fakeRequest, messages)

      behave like normalPage(
        applyView,
        None,
        "matchingDown",
        "p1", "p2.beforeLink", "p2.link", "p2.afterLink"
      )

      behave like pageWithASignOutButton(applyView)
    }
  }
}
