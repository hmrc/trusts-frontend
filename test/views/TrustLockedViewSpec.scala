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

import views.behaviours.ViewBehaviours
import views.html.TrustLockedView

class TrustLockedViewSpec extends ViewBehaviours {

  val utr = "0987654321"

  "TrustLocked view" must {

    val view = viewFor[TrustLockedView](Some(emptyUserAnswers))

    val applyView = view.apply(utr)(fakeRequest, messages)

    behave like normalPage(applyView, "TrustLocked","p1", "p2","p3",
      "link1")

    "display the correct subheading" in {
      val doc = asDocument(applyView)
      assertContainsText(doc, messages("TrustLocked.subheading", utr))
    }

  }

}