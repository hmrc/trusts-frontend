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

import java.time.LocalDateTime

import utils.TestUserAnswers
import viewmodels.DraftRegistration
import views.behaviours.ViewBehaviours
import views.html.AgentOverviewView

class AgentOverviewViewSpec extends ViewBehaviours {

  "AgentOverview view" must {

    val view = viewFor[AgentOverviewView](Some(emptyUserAnswers))

    val applyView = view.apply(List(DraftRegistration("Fake", "Fake2", LocalDateTime.now.toString)))(fakeRequest, messages)

    behave like normalPage(applyView, "agentOverview",
      "paragraph1",
      "helper",
      "paragraph2",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "bulletpoint4",
      "paragraph3",
      "bulletpoint5",
      "bulletpoint6",
      "bulletpoint7"
    )

    behave like pageWithBackLink(applyView)
  }

  "render draft registrations" in {

    val drafts = List(
      DraftRegistration("FakeDraftId1", "Fake2", LocalDateTime.now.toString),
      DraftRegistration("FakeDraftId2", "Fake2", LocalDateTime.now.toString),
      DraftRegistration("FakeDraftId3", "Fake2", LocalDateTime.now.toString)
    )

    val view = viewFor[AgentOverviewView](Some(emptyUserAnswers))

    val applyView = view.apply(drafts)(fakeRequest, messages)

    val doc = asDocument(applyView)

    assertContainsText(doc, "FakeDraftId1")
    assertContainsText(doc, "FakeDraftId2")
    assertContainsText(doc, "FakeDraftId3")
  }
}
