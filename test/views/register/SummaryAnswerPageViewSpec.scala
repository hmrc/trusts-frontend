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

import java.time.LocalDateTime
import pages.register._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers
import utils.print.register.PrintUserAnswersHelper
import views.behaviours.ViewBehaviours
import views.html.register.SummaryAnswerPageView

class SummaryAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "SummaryAnswerPage view" must {

    val userAnswers = TestUserAnswers.emptyUserAnswers
      .set(RegistrationTRNPage, "XNTRN000000001").success.value
      .set(RegistrationSubmissionDatePage, LocalDateTime.of(2010, 10, 10, 13, 10, 10)).success.value

    val view = viewFor[SummaryAnswerPageView](Some(userAnswers))

    val app = applicationBuilder().build()

    val helper = app.injector.instanceOf[PrintUserAnswersHelper]

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val summary = helper.summary(fakeDraftId)

    val orgDoc = {
      summary.map {
        sections =>
          val applyOrganisationView = view.apply(sections, isAgent = false, "")(fakeRequest, messages)

          behave like normalPage(applyOrganisationView, None, "summaryAnswerPage", "paragraph1", "paragraph2")

          //behave like pageWithReturnToTopLink(applyOrganisationView)

          asDocument(applyOrganisationView)
      }
    }

    val agentDoc = {
      summary.map {
        sections =>
          val applyAgentView = view.apply(sections, isAgent = true, "agentClientReference")(fakeRequest, messages)

          //behave like pageWithReturnToTopLink(applyAgentView)

          asDocument(applyAgentView)
      }
    }

    "assert header content for Agent user" in {
      agentDoc.map(assertContainsText(_, messages("answerPage.agentClientRef", "agentClientReference")))
    }

    "assert correct number of headers and subheaders for Agent user" in {
      agentDoc.map {
        doc =>
          val wrapper = doc.getElementById("wrapper")
          val headers = wrapper.getElementsByTag("h2")
          val subHeaders = wrapper.getElementsByTag("h3")

          headers.size mustBe 5
          subHeaders.size mustBe 3
      }
    }

    "assert correct number of headers and subheaders for Organisation user" in {
      orgDoc.map {
        doc =>
          val wrapper = doc.getElementById("wrapper")
          val headers = wrapper.getElementsByTag("h2")
          val subHeaders = wrapper.getElementsByTag("h3")

          headers.size mustBe 4
          subHeaders.size mustBe 3
      }
    }

    "assert back to top link present for Agent user" in {
      orgDoc.map {
        doc =>
          //val returnToTopLink = doc.getElementById("return-to-top")

          assertRenderedById(doc, "return-to-top")
      }
    }
//
//    "assert back to top link present for Organisation user" in {
//      summary.map {
//        sections =>
//          val applyOrganisationView = view.apply(sections, isAgent = false, "")(fakeRequest, messages)
//
//          val doc = asDocument(applyOrganisationView)
//          assertRenderedById(doc, "return-to-top")
//      }
//    }

  }
}
