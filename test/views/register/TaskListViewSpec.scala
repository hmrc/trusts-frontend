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

import controllers.register.agents.routes
import navigation.registration.TaskListNavigator
import pages.register.RegistrationProgress
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.Task
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.register.TaskListView

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.Future

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.format(dateFormatter)
  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def newRegistrationProgress = new RegistrationProgress(new TaskListNavigator(fakeFrontendAppConfig), registrationsRepository)

  private lazy val sections: Future[List[Task]] = newRegistrationProgress.items(fakeDraftId)
  private lazy val additionalSections: Future[List[Task]] = newRegistrationProgress.additionalItems(fakeDraftId)
  private def isTaskListComplete: Future[Boolean] = newRegistrationProgress.isTaskListComplete(fakeDraftId, Agent)

  "TaskList view" when {

    "rendered for an Organisation or an Agent" must {

      "render sections" in {

        val answers = emptyUserAnswers

        val view = viewFor[TaskListView](Some(answers))

        for {
          sections <- sections
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete
        } yield {

          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Organisation
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, sections ++ additionalSections)
        }
      }

      "render summary" when {

        "all sections are completed" in {

          for {
            sections <- sections
            additionalSections <- additionalSections
            isTaskListComplete <- isTaskListComplete
          } yield {
            val view = viewFor[TaskListView](Some(emptyUserAnswers))
            val applyView = view.apply(
              fakeDraftId,
              savedUntil,
              sections,
              additionalSections,
              isTaskListComplete,
              Organisation
            )(fakeRequest, messages)
            val doc = asDocument(applyView)

            assertRenderedById(doc, "summaryHeading")
            assertRenderedById(doc, "summary-paragraph")
            assertRenderedById(doc, "summaryHeading2")
            assertRenderedById(doc, "summary-paragraph-2")
            assertRenderedById(doc, "print-and-save")

          }
        }
      }

      "not render summary" when {

        "not all sections are completed" in {

          for {
            sections <- sections
            additionalSections <- additionalSections
            isTaskListComplete <- isTaskListComplete
          } yield {
            val view = viewFor[TaskListView](Some(emptyUserAnswers))
            val applyView = view.apply(
              fakeDraftId,
              savedUntil,
              sections,
              additionalSections,
              isTaskListComplete,
              Organisation
            )(fakeRequest, messages)
            val doc = asDocument(applyView)

            assertNotRenderedById(doc, "summaryHeading")
            assertNotRenderedById(doc, "summary-paragraph")
            assertNotRenderedById(doc, "summaryHeading2")
            assertNotRenderedById(doc, "summary-paragraph-2")
            assertNotRenderedById(doc, "print-and-save")

          }
        }
      }

    }

    "rendered for an Organisation" must {

      "render Saved Until" in {
        for {
          sections <- sections
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete
        } yield {
          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Organisation
          )(fakeRequest, messages)

          val doc = asDocument(applyView)
          assertRenderedById(doc, "saved-until")
        }
      }
    }

    "rendered for an Agent" must {

      "render return to saved registrations link" in {
        for {
          sections <- sections
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete
        } yield {
          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Agent
          )(fakeRequest, messages)

          val doc = asDocument(applyView)

          assertAttributeValueForElement(
            doc.getElementById("saved-registrations"),
            "href",
            routes.AgentOverviewController.onPageLoad().url
          )
        }
      }

      "render agent details link" in {
        for {
          sections <- sections
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete
        } yield {
          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Agent
          )(fakeRequest, messages)

          val doc = asDocument(applyView)

          assertAttributeValueForElement(
            doc.getElementById("agent-details"),
            "href",
            fakeFrontendAppConfig.agentDetailsFrontendUrl(fakeDraftId)
          )
        }
      }

      "not render saved until" in {
        for {
          sections <- sections
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete
        } yield {
          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Agent
          )(fakeRequest, messages)

          val doc = asDocument(applyView)
          assertNotRenderedById(doc, "saved-until")
        }
      }
    }
  }
}
