/*
 * Copyright 2024 HM Revenue & Customs
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

import java.time.LocalDateTime
import pages.register._
import utils.TestUserAnswers
import views.behaviours.ViewBehaviours
import views.html.register.SummaryAnswerPageView

class SummaryAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "SummaryAnswerPage view" must {

    val userAnswers = TestUserAnswers.emptyUserAnswers
      .set(RegistrationTRNPage, "XNTRN000000001")
      .success
      .value
      .set(RegistrationSubmissionDatePage, LocalDateTime.of(2010, 10, 10, 13, 10, 10))
      .success
      .value

    val view = viewFor[SummaryAnswerPageView](Some(userAnswers))

    val applyAgentView = view.apply(Nil, isAgent = true, "agentClientReference")(fakeRequest, messages)

    val applyOrganisationView = view.apply(Nil, isAgent = false, "")(fakeRequest, messages)

    val agentDoc = asDocument(applyAgentView)

    val orgDoc = asDocument(applyOrganisationView)

    behave like normalPage(applyOrganisationView, None, "summaryAnswerPage", "paragraph1", "paragraph2")

    "assert header content for Agent user" in
      assertContainsText(agentDoc, messages("answerPage.agentClientRef", "agentClientReference"))

    "assert agent header present for Agent user" in {
      val headers = agentDoc.getElementsByClass("agentClientRef")

      headers.size mustBe 1
    }

    "assert agent header not present for Organisation user" in {
      val headers = orgDoc.getElementsByClass("agentClientRef")

      headers.size mustBe 0
    }

    "assert back to top link present for Agent user" in
      assertRenderedById(agentDoc, "return-to-top")

    "assert back to top link present for Organisation user" in
      assertRenderedById(orgDoc, "return-to-top")

  }

}
