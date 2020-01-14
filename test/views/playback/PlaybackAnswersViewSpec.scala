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

package views.playback

import views.behaviours.ViewBehaviours
import views.html.playback.PlaybackAnswersView

class PlaybackAnswersViewSpec extends ViewBehaviours {

  "PlaybackAnswerView view" must {

    val view = viewFor[PlaybackAnswersView](Some(emptyUserAnswers))

    val applyView = view.apply(Nil, Nil)(fakeRequest, messages)

    behave like normalPage(applyView, "playbackLastDeclared")

    "render correct content" in {
      val doc = asDocument(applyView)

      assertContainsText(doc,
        "You only need to declare the trust is up to date every year if there is a tax liability"
      )

      assertContainsText(doc,
        "The following sections indicate what was entered when the trust was first registered." +
        " No further updates to trust details, assets and years of tax liability are required through this service."
      )
      assertContainsText(doc,
        "If you need to view the latest information HMRC holds about these sections or need to update them," +
        " use Self Assessment Online for trusts."
      )
    }
  }
}
