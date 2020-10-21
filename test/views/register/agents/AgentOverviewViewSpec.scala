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

package views.register.agents

import java.time.LocalDateTime

import viewmodels.DraftRegistration
import views.behaviours.ViewBehaviours
import views.html.register.agents.AgentOverviewView

class AgentOverviewViewSpec extends ViewBehaviours {

  "AgentOverview view" must {

    val view = viewFor[AgentOverviewView](Some(emptyUserAnswers))

    val applyView = view.apply(List(DraftRegistration("Fake", Some("Fake2"), LocalDateTime.now.toString)))(fakeRequest, messages)

    behave like normalPage(applyView, None, "agentOverview",
      "paragraph1",
      "paragraph2",
      "paragraph3",
      "paragraph4"

    )

    behave like pageWithBackLink(applyView)
  }

  "not render 'Saved registrations' h2 when no draft registrations present" in {

    val view = viewFor[AgentOverviewView](None)
    val applyView = view.apply(Nil)(fakeRequest, messages)
    val doc = asDocument(applyView)

    assertNotRenderedByClass(doc, "saved-registration")
  }

  "render draft registrations" in {

    val drafts = List(
      DraftRegistration("FakeDraftId1", Some("Fake2"), LocalDateTime.now.toString),
      DraftRegistration("FakeDraftId2", Some("Fake2"), LocalDateTime.now.toString),
      DraftRegistration("FakeDraftId3", Some("Fake2"), LocalDateTime.now.toString)
    )

    val view = viewFor[AgentOverviewView](Some(emptyUserAnswers))

    val applyView = view.apply(drafts)(fakeRequest, messages)

    val doc = asDocument(applyView)

    assertContainsText(doc, "Saved registrations")
    assertContainsText(doc, "FakeDraftId1")
    assertContainsText(doc, "FakeDraftId2")
    assertContainsText(doc, "FakeDraftId3")
  }
}
