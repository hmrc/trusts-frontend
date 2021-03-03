/*
 * Copyright 2021 HM Revenue & Customs
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
import views.html.register.RefSentByPostView

class RefSentByPostViewSpec extends ViewBehaviours {

  "RefSentByPost view" must {

    val view = viewFor[RefSentByPostView](Some(emptyUserAnswers))

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView,
      None, "refSentByPost", "paragraph1",
      "subheading1",
      "paragraph2",
      "bullet1",
      "paragraph3",
      "subheading2",
      "paragraph4",
      "bullet2",
      "subheading3",
      "paragraph5",
      "paragraph5.link",
      "paragraph6",
      "paragraph6.link"
    )

    behave like pageWithBackLink(applyView)
  }
}
