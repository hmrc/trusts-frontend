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
import java.time.format.DateTimeFormatter

import controllers.routes
import viewmodels._
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.format(dateFormatter)

  "TaskList view" when {

    "rendered for an Organisation or an Agent" when {
        val expectedSections = List(
          Task(Link("trust-details", routes.AddATrusteeController.onPageLoad().url), Some(Completed)),
          Task(Link("settlors", routes.AddATrusteeController.onPageLoad().url), Some(InProgress)),
          Task(Link("trustees", routes.AddATrusteeController.onPageLoad().url), Some(InProgress)),
          Task(Link("beneficiaries", routes.AddATrusteeController.onPageLoad().url), None),
          Task(Link("assets", routes.AddATrusteeController.onPageLoad().url), None),
          Task(Link("tax-liability", routes.AddATrusteeController.onPageLoad().url), Some(Completed))
        )

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val view = application.injector.instanceOf[TaskListView]

        application.stop()

        val applyView = view.apply(savedUntil, expectedSections)(fakeRequest, messages)

        behave like normalPage(applyView, "taskList")

        behave like pageWithBackLink(applyView)

        behave like taskList(applyView, expectedSections)
      }

    "rendered for an Agent" must {

      "render return to saved registrations link" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val view = application.injector.instanceOf[TaskListView]

        val agentLinks = AgentLinks(
          Link("taskList.agent.savedRegistrations", routes.AgentOverviewController.onPageLoad().url),
          Link("taskList.agent.agentDetails", routes.AgentOverviewController.onPageLoad().url)
        )

        val applyView = view.apply(savedUntil, Nil, Some(agentLinks))(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertAttributeValueForElement(
          doc.getElementById("saved-registrations"),
          "href",
          routes.AgentOverviewController.onPageLoad().url
        )

        application.stop()
      }

      "render agent details link" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val view = application.injector.instanceOf[TaskListView]

        val agentLinks = AgentLinks(
          Link("taskList.agent.savedRegistrations", routes.AgentOverviewController.onPageLoad().url),
          Link("taskList.agent.agentDetails", routes.AgentOverviewController.onPageLoad().url)
        )

        val applyView = view.apply(savedUntil, Nil, Some(agentLinks))(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertAttributeValueForElement(
          doc.getElementById("agent-details"),
          "href",
          routes.AgentOverviewController.onPageLoad().url
        )

        application.stop()
      }

    }

  }

}
