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
import viewmodels.{Completed, InProgress, Task}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.format(dateFormatter)

  "TaskList view" must {

    "rendered for an Organisation or an Agent" when {
        val expectedSections = List(
          Task("trust-details", routes.AddATrusteeController.onPageLoad(), Some(Completed)),
          Task("settlors", routes.AddATrusteeController.onPageLoad(), Some(InProgress)),
          Task("trustees", routes.AddATrusteeController.onPageLoad(), Some(InProgress)),
          Task("beneficiaries", routes.AddATrusteeController.onPageLoad(), None),
          Task("assets", routes.AddATrusteeController.onPageLoad(), None),
          Task("tax-liability", routes.AddATrusteeController.onPageLoad(), Some(Completed))
        )

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val view = application.injector.instanceOf[TaskListView]

        application.stop()

        val applyView = view.apply(savedUntil, expectedSections)(fakeRequest, messages)

        behave like normalPage(applyView, "taskList")

        behave like pageWithBackLink(applyView)

        behave like taskList(applyView, expectedSections)
      }
    }

  "rendered for an Agent" must {

    "render return to saved registrations link" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val view = application.injector.instanceOf[TaskListView]

      val applyView = view.apply(savedUntil, Nil)(fakeRequest, messages)

      val doc = asDocument(applyView)

      assertAttributeValueForElement(
        doc.getElementById("saved-registrations"),
        "href",
        ""
      )

      application.stop()
    }

  }

}
