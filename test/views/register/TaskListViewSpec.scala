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
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import viewmodels.{Link, Task}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.register.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val savedUntil = "21 April 2021"

  private val fakeSections = List(
    Task(Link("link 1", "url 1"), None),
    Task(Link("link 2", "url 2"), None)
  )

  private val fakeAdditionalSections = List(
    Task(Link("additional link 1", "additional url 1"), None),
    Task(Link("additional link 2", "additional url 2"), None)
  )

  "TaskListView" when {

    "deployment notification is enabled" must {
      "render warning and notification" in {
        val application = applicationBuilder(Some(emptyUserAnswers))
          .configure(
            "microservice.services.features.deployment.notification.enabled" -> true
          ).build()

        val view = application.injector.instanceOf[TaskListView]

        val appliedView = view.apply(
          isTaxable = true,
          draftId = fakeDraftId,
          savedUntil = savedUntil,
          sections = Nil,
          additionalSections = Nil,
          isTaskListComplete = false,
          affinityGroup = Organisation
        )(fakeRequest, messages)

        val doc = asDocument(appliedView)

        assertContainsText(doc, "The Trust Registration Service will not be available from 29 April to 4 May. This is to allow HMRC to make essential changes to the service.")
        assertContainsText(doc, "You need to complete any partially completed trust registrations by 28 April, 4:30PM. Any incomplete registrations will be deleted after this time.")

        application.stop()
      }
    }

    "organisation" when {

      "task list complete" when {

        "taxable" must {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))

          val applyView = view.apply(
            isTaxable = true,
            draftId = fakeDraftId,
            savedUntil = savedUntil,
            sections = fakeSections,
            additionalSections = fakeAdditionalSections,
            isTaskListComplete = true,
            affinityGroup = Organisation
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

          "display correct content" in {
            val doc = asDocument(applyView)

            assertRenderedById(doc, "saved-until")

            assertRenderedById(doc, "summaryHeading")
            assertRenderedById(doc, "summary-paragraph")
            assertRenderedById(doc, "summaryHeading2")
            assertRenderedById(doc, "summary-paragraph-2")
            assertRenderedById(doc, "print-and-save")
          }
        }

        "non-taxable" must {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))

          val applyView = view.apply(
            isTaxable = false,
            draftId = fakeDraftId,
            savedUntil = savedUntil,
            sections = fakeSections,
            additionalSections = fakeAdditionalSections,
            isTaskListComplete = true,
            affinityGroup = Organisation
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

          "display correct content" in {
            val doc = asDocument(applyView)

            assertRenderedById(doc, "saved-until")

            assertRenderedById(doc, "summaryHeading")
            assertRenderedById(doc, "summary-paragraph")
            assertNotRenderedById(doc, "summaryHeading2")
            assertNotRenderedById(doc, "summary-paragraph-2")
            assertRenderedById(doc, "print-and-save")
          }
        }
      }

      "task list incomplete" when {

        "taxable" must {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))

          val applyView = view.apply(
            isTaxable = true,
            draftId = fakeDraftId,
            savedUntil = savedUntil,
            sections = fakeSections,
            additionalSections = fakeAdditionalSections,
            isTaskListComplete = false,
            affinityGroup = Organisation
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

          "display correct content" in {
            val doc = asDocument(applyView)

            assertRenderedById(doc, "saved-until")

            assertNotRenderedById(doc, "summaryHeading")
            assertNotRenderedById(doc, "summary-paragraph")
            assertNotRenderedById(doc, "summaryHeading2")
            assertNotRenderedById(doc, "summary-paragraph-2")
            assertNotRenderedById(doc, "print-and-save")
          }
        }

        "non-taxable" must {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))

          val applyView = view.apply(
            isTaxable = false,
            draftId = fakeDraftId,
            savedUntil = savedUntil,
            sections = fakeSections,
            additionalSections = fakeAdditionalSections,
            isTaskListComplete = false,
            affinityGroup = Organisation
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

          "display correct content" in {
            val doc = asDocument(applyView)

            assertRenderedById(doc, "saved-until")

            assertNotRenderedById(doc, "summaryHeading")
            assertNotRenderedById(doc, "summary-paragraph")
            assertNotRenderedById(doc, "summaryHeading2")
            assertNotRenderedById(doc, "summary-paragraph-2")
            assertNotRenderedById(doc, "print-and-save")
          }
        }
      }
    }

    "agent" when {

      "task list complete" when {

        "taxable" must {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))

          val applyView = view.apply(
            isTaxable = true,
            draftId = fakeDraftId,
            savedUntil = savedUntil,
            sections = fakeSections,
            additionalSections = fakeAdditionalSections,
            isTaskListComplete = true,
            affinityGroup = Agent
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

          "display correct content" in {
            val doc = asDocument(applyView)

            assertAttributeValueForElement(
              doc.getElementById("saved-registrations"),
              "href",
              routes.AgentOverviewController.onPageLoad().url
            )

            assertAttributeValueForElement(
              doc.getElementById("agent-details"),
              "href",
              fakeFrontendAppConfig.agentDetailsFrontendUrl(fakeDraftId)
            )

            assertNotRenderedById(doc, "saved-until")

            assertRenderedById(doc, "summaryHeading")
            assertRenderedById(doc, "summary-paragraph")
            assertRenderedById(doc, "summaryHeading2")
            assertRenderedById(doc, "summary-paragraph-2")
            assertRenderedById(doc, "print-and-save")
          }
        }

        "non-taxable" must {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))

          val applyView = view.apply(
            isTaxable = false,
            draftId = fakeDraftId,
            savedUntil = savedUntil,
            sections = fakeSections,
            additionalSections = fakeAdditionalSections,
            isTaskListComplete = true,
            affinityGroup = Agent
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

          "display correct content" in {
            val doc = asDocument(applyView)

            assertAttributeValueForElement(
              doc.getElementById("saved-registrations"),
              "href",
              routes.AgentOverviewController.onPageLoad().url
            )

            assertAttributeValueForElement(
              doc.getElementById("agent-details"),
              "href",
              fakeFrontendAppConfig.agentDetailsFrontendUrl(fakeDraftId)
            )

            assertNotRenderedById(doc, "saved-until")

            assertRenderedById(doc, "summaryHeading")
            assertRenderedById(doc, "summary-paragraph")
            assertNotRenderedById(doc, "summaryHeading2")
            assertNotRenderedById(doc, "summary-paragraph-2")
            assertRenderedById(doc, "print-and-save")
          }
        }
      }

      "task list incomplete" when {

        "taxable" must {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))

          val applyView = view.apply(
            isTaxable = true,
            draftId = fakeDraftId,
            savedUntil = savedUntil,
            sections = fakeSections,
            additionalSections = fakeAdditionalSections,
            isTaskListComplete = false,
            affinityGroup = Agent
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

          "display correct content" in {
            val doc = asDocument(applyView)

            assertAttributeValueForElement(
              doc.getElementById("saved-registrations"),
              "href",
              routes.AgentOverviewController.onPageLoad().url
            )

            assertAttributeValueForElement(
              doc.getElementById("agent-details"),
              "href",
              fakeFrontendAppConfig.agentDetailsFrontendUrl(fakeDraftId)
            )

            assertNotRenderedById(doc, "saved-until")

            assertNotRenderedById(doc, "summaryHeading")
            assertNotRenderedById(doc, "summary-paragraph")
            assertNotRenderedById(doc, "summaryHeading2")
            assertNotRenderedById(doc, "summary-paragraph-2")
            assertNotRenderedById(doc, "print-and-save")
          }
        }

        "non-taxable" must {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))

          val applyView = view.apply(
            isTaxable = false,
            draftId = fakeDraftId,
            savedUntil = savedUntil,
            sections = fakeSections,
            additionalSections = fakeAdditionalSections,
            isTaskListComplete = false,
            affinityGroup = Agent
          )(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

          "display correct content" in {
            val doc = asDocument(applyView)

            assertAttributeValueForElement(
              doc.getElementById("saved-registrations"),
              "href",
              routes.AgentOverviewController.onPageLoad().url
            )

            assertAttributeValueForElement(
              doc.getElementById("agent-details"),
              "href",
              fakeFrontendAppConfig.agentDetailsFrontendUrl(fakeDraftId)
            )

            assertNotRenderedById(doc, "saved-until")

            assertNotRenderedById(doc, "summaryHeading")
            assertNotRenderedById(doc, "summary-paragraph")
            assertNotRenderedById(doc, "summaryHeading2")
            assertNotRenderedById(doc, "summary-paragraph-2")
            assertNotRenderedById(doc, "print-and-save")
          }
        }
      }
    }
  }
}
