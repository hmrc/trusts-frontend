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

import controllers.register.agents.routes
import models.registration.pages.TagStatus.NotStarted
import org.jsoup.nodes.Document
import org.scalatest.Assertion
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import viewmodels.{Link, Task}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.register.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val savedUntil = "21 April 2021"
  private val completedTasks = 2
  private val totalTasks = 7

  private val fakeSections = List(
    Task(Link("link 1", "url 1"), NotStarted),
    Task(Link("link 2", "url 2"), NotStarted)
  )

  private val fakeAdditionalSections = List(
    Task(Link("additional link 1", "additional url 1"), NotStarted),
    Task(Link("additional link 2", "additional url 2"), NotStarted)
  )

  private def assertionForSavedUntil(doc: Document, rendered: Boolean): Assertion = {
    def makeAssertion(assertion: (Document, String) => Assertion): Assertion = {
      assertion(doc, "saved-until")
    }

    assertionForId(rendered, makeAssertion)
  }

  private def assertionForPrintACopySection(doc: Document, rendered: Boolean): Assertion = {
    def makeAssertion(assertion: (Document, String) => Assertion): Assertion = {
      assertion(doc, messages("taskList.summary.heading1"))
      assertion(doc, messages("taskList.summary.link1"))
      assertion(doc, messages("taskList.summary.paragraph1.end"))
    }

    assertionForText(rendered, makeAssertion)
  }

  private def assertionForUpdateAssetDetailsSection(doc: Document, rendered: Boolean): Assertion = {
    def makeAssertion(assertion: (Document, String) => Assertion): Assertion = {
      assertion(doc, messages("taskList.summary.heading2"))
      assertion(doc, messages("taskList.summary.paragraph2"))
      assertion(doc, messages("taskList.summary.bullet1"))
      assertion(doc, messages("taskList.summary.bullet2"))
    }

    assertionForText(rendered, makeAssertion)
  }

  private def assertionForNonEeaCompanySection(doc: Document, rendered: Boolean): Assertion = {
    def makeAssertion(assertion: (Document, String) => Assertion): Assertion = {
      assertion(doc, messages("taskList.summary.heading3"))
      assertion(doc, messages("taskList.summary.paragraph3"))
    }

    assertionForText(rendered, makeAssertion)
  }
  
  private def assertSavedRegistrationsAndAgentDetailsRendered(doc: Document): Assertion = {
    assertAttributeValueForElement(
      element = doc.getElementById("saved-registrations"),
      attribute = "href",
      attributeValue = routes.AgentOverviewController.onPageLoad().url
    )

    assertAttributeValueForElement(
      element = doc.getElementById("agent-details"),
      attribute = "href",
      attributeValue = fakeFrontendAppConfig.agentDetailsFrontendUrl(fakeDraftId)
    )
  }

  private def assertionForPrintAndSave(doc: Document, rendered: Boolean): Assertion = {
    def makeAssertion(assertion: (Document, String) => Assertion): Assertion = {
      assertion(doc, "print-and-save")
    }

    assertionForId(rendered, makeAssertion)
  }

  private def assertionForText(rendered: Boolean,
                               makeAssertion: ((Document, String) => Assertion) => Assertion): Assertion = {
    if (rendered) {
      makeAssertion(assertContainsText)
    } else {
      makeAssertion(assertDoesNotContainText)
    }
  }

  private def assertionForId(rendered: Boolean,
                             makeAssertion: ((Document, String) => Assertion) => Assertion): Assertion = {
    if (rendered) {
      makeAssertion(assertRenderedById)
    } else {
      makeAssertion(assertNotRenderedById)
    }
  }

  private def assertTaskProgressMessage(doc: Document): Assertion = {
    val expectedMessage = messages("taskList.incompleteSections.label", completedTasks, totalTasks)
    assertContainsText(doc, expectedMessage)
  }

  "TaskListView" when {

    "deployment notification is enabled" must {
      "render warning and notification" in {

        val application = new GuiceApplicationBuilder()
          .configure(
            "microservice.services.features.deployment.notification.enabled" -> true
          ).build()

        val view = application.injector.instanceOf[TaskListView]

        val applyView = view.apply(
          isTaxable = true,
          draftId = fakeDraftId,
          savedUntil = savedUntil,
          sections = Nil,
          additionalSections = Nil,
          isTaskListComplete = false,
          affinityGroup = Organisation,
          completedTasks = completedTasks,
          totalTasks = totalTasks
        )(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertContainsText(doc, messages("notification.instruction"))
        assertContainsText(doc, messages("notification.warning"))

        application.stop()
      }
    }

    "deployment notification is disabled" when {

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
                affinityGroup = Organisation,
                completedTasks = completedTasks,
                totalTasks = totalTasks
              )(fakeRequest, messages)

              behave like normalPage(applyView, None, "taskList", "paragraph1")

              behave like pageWithBackLink(applyView)

              behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

              "display correct content" in {
                val doc = asDocument(applyView)

                assertionForSavedUntil(doc, rendered = true)
                assertionForPrintACopySection(doc, rendered = true)
                assertionForUpdateAssetDetailsSection(doc, rendered = true)
                assertionForNonEeaCompanySection(doc, rendered = true)
                assertionForPrintAndSave(doc, rendered = true)
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
                affinityGroup = Organisation,
                completedTasks = completedTasks,
                totalTasks = totalTasks
              )(fakeRequest, messages)

              behave like normalPage(applyView, None, "taskList", "paragraph1")

              behave like pageWithBackLink(applyView)

              behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

              "display correct content" in {
                val doc = asDocument(applyView)

                assertionForSavedUntil(doc, rendered = true)
                assertionForPrintACopySection(doc, rendered = true)
                assertionForUpdateAssetDetailsSection(doc, rendered = false)
                assertionForNonEeaCompanySection(doc, rendered = true)
                assertionForPrintAndSave(doc, rendered = true)
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
                affinityGroup = Organisation,
                completedTasks = completedTasks,
                totalTasks = totalTasks
              )(fakeRequest, messages)

              behave like normalPage(applyView, None, "taskList", "paragraph1")

              behave like pageWithBackLink(applyView)

              behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

              "display correct content" in {
                val doc = asDocument(applyView)

                assertionForSavedUntil(doc, rendered = true)
                assertionForPrintACopySection(doc, rendered = false)
                assertionForUpdateAssetDetailsSection(doc, rendered = false)
                assertionForNonEeaCompanySection(doc, rendered = false)
                assertionForPrintAndSave(doc, rendered = false)
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
                affinityGroup = Organisation,
                completedTasks = completedTasks,
                totalTasks = totalTasks
              )(fakeRequest, messages)

              behave like normalPage(applyView, None, "taskList", "paragraph1")

              behave like pageWithBackLink(applyView)

              behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

              "display correct content" in {
                val doc = asDocument(applyView)

                assertionForSavedUntil(doc, rendered = true)
                assertionForPrintACopySection(doc, rendered = false)
                assertionForUpdateAssetDetailsSection(doc, rendered = false)
                assertionForNonEeaCompanySection(doc, rendered = false)
                assertionForPrintAndSave(doc, rendered = false)
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
                affinityGroup = Agent,
                completedTasks = completedTasks,
                totalTasks = totalTasks
              )(fakeRequest, messages)

              behave like normalPage(applyView, None, "taskList", "paragraph1")

              behave like pageWithBackLink(applyView)

              behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

              "display correct content" in {
                val doc = asDocument(applyView)

                assertSavedRegistrationsAndAgentDetailsRendered(doc)
                assertionForSavedUntil(doc, rendered = false)
                assertionForPrintACopySection(doc, rendered = true)
                assertionForUpdateAssetDetailsSection(doc, rendered = true)
                assertionForNonEeaCompanySection(doc, rendered = true)
                assertionForPrintAndSave(doc, rendered = true)
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
                affinityGroup = Agent,
                completedTasks = completedTasks,
                totalTasks = totalTasks
              )(fakeRequest, messages)

              behave like normalPage(applyView, None, "taskList", "paragraph1")

              behave like pageWithBackLink(applyView)

              behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

              "display correct content" in {
                val doc = asDocument(applyView)

                assertSavedRegistrationsAndAgentDetailsRendered(doc)
                assertionForSavedUntil(doc, rendered = false)
                assertionForPrintACopySection(doc, rendered = true)
                assertionForUpdateAssetDetailsSection(doc, rendered = false)
                assertionForNonEeaCompanySection(doc, rendered = true)
                assertionForPrintAndSave(doc, rendered = true)
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
                affinityGroup = Agent,
                completedTasks = completedTasks,
                totalTasks = totalTasks
              )(fakeRequest, messages)

              behave like normalPage(applyView, None, "taskList", "paragraph1")

              behave like pageWithBackLink(applyView)

              behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

              "display correct content" in {
                val doc = asDocument(applyView)

                assertSavedRegistrationsAndAgentDetailsRendered(doc)
                assertionForSavedUntil(doc, rendered = false)
                assertionForPrintACopySection(doc, rendered = false)
                assertionForUpdateAssetDetailsSection(doc, rendered = false)
                assertionForNonEeaCompanySection(doc, rendered = false)
                assertionForPrintAndSave(doc, rendered = false)
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
                affinityGroup = Agent,
                completedTasks = completedTasks,
                totalTasks = totalTasks
              )(fakeRequest, messages)

              behave like normalPage(applyView, None, "taskList", "paragraph1")

              behave like pageWithBackLink(applyView)

              behave like taskList(applyView, fakeSections ++ fakeAdditionalSections)

              "display correct content" in {
                val doc = asDocument(applyView)

                assertSavedRegistrationsAndAgentDetailsRendered(doc)
                assertionForSavedUntil(doc, rendered = false)
                assertionForPrintACopySection(doc, rendered = false)
                assertionForUpdateAssetDetailsSection(doc, rendered = false)
                assertionForNonEeaCompanySection(doc, rendered = false)
                assertionForPrintAndSave(doc, rendered = false)
              }
            }
          }
        }
    }
  }
}
