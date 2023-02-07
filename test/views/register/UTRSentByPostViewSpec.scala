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

import views.behaviours.ViewBehaviours
import views.html.register.UTRSentByPostView

class UTRSentByPostViewSpec extends ViewBehaviours {

  "UTRSentByPost view" must {

    val view = viewFor[UTRSentByPostView](Some(emptyUserAnswers))

    val applyView = view.apply(isAgent = false)(fakeRequest, messages)

    behave like normalPage(applyView, None, "UTRSentByPost", "paragraph1", "paragraph2")

    behave like pageWithBackLink(applyView)
  }

  "when rendered for an Organisation" must {

    "not render Return to register paragraph" in {
      val view = viewFor[UTRSentByPostView](Some(emptyUserAnswers))
      val applyView = view.apply(isAgent = false)(fakeRequest, messages)

      val doc = asDocument(applyView)
      assertNotRenderedById(doc, "saved-registrations")
    }

  }

  "when rendered for an Agent" must {

    "render Return to register paragraph" in {
      val view = viewFor[UTRSentByPostView](Some(emptyUserAnswers))
      val applyView = view.apply(isAgent = true)(fakeRequest, messages)

      val doc = asDocument(applyView)
      assertRenderedById(doc, "saved-registrations")
    }
  }
}
