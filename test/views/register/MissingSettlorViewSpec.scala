/*
 * Copyright 2026 HM Revenue & Customs
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
import views.html.register.MissingSettlorView

class MissingSettlorViewSpec extends ViewBehaviours {

  "MissingSettlorView view" must {

    val trn = "XATRUST00000001"

    val application = applicationBuilder().build()

    val view = application.injector.instanceOf[MissingSettlorView]

    val applyView = view.apply(trn)(fakeRequest, messages)

    behave like normalPage(
      applyView,
      None,
      "missingSettlorView",
      "p1",
      "p2",
      "h2",
      "p3",
      "p4",
      "p5",
      "p6",
      "p7",
      "p8",
      "p9"
    )

    behave like pageWithBackLink(applyView)

    "display the TRN" in {
      val doc = asDocument(applyView)
      assertContainsText(doc, trn)
    }
  }

}
