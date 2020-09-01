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

package views.register

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import controllers.register.agents.routes
import models.core.UserAnswers
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages._
import navigation.registration.TaskListNavigator
import pages.entitystatus._
import pages.register.RegistrationProgress
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.trust_details.WhenTrustSetupPage
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.http.HeaderCarrier
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.register.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.format(dateFormatter)
  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def newRegistrationProgress = new RegistrationProgress(new TaskListNavigator(frontendAppConfig), registrationsRepository)

  private def sections(answers: UserAnswers) = newRegistrationProgress.items(answers,fakeDraftId)
  private lazy val additionalSections = newRegistrationProgress.additionalItems(fakeDraftId)
  private def isTaskListComplete(answers: UserAnswers) = newRegistrationProgress.isTaskListComplete(answers)

  "TaskList view" when {

    "rendered for an Organisation or an Agent" must {

      "render sections" in {

        val answers = emptyUserAnswers

        val view = viewFor[TaskListView](Some(answers))

        for {
          sections <- sections(answers)
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete(answers)
        } yield {

          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Organisation)(fakeRequest, messages)

          behave like normalPage(applyView, None, "taskList")

          behave like pageWithBackLink(applyView)

          behave like taskList(applyView, sections ++ additionalSections)
        }
      }

      "render summary" when {

        "all sections are completed" in {

          val userAnswers = emptyUserAnswers
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(TrustDetailsStatus, Completed).success.value
            .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
            .set(DeceasedSettlorStatus, Status.Completed).success.value
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(AssetMoneyValuePage(0), "2000").success.value
            .set(AssetStatus(0), Completed).success.value
            .set(AddAssetsPage, NoComplete).success.value

          for {
            sections <- sections(userAnswers)
            additionalSections <- additionalSections
            isTaskListComplete <- isTaskListComplete(userAnswers)
          } yield {
            val view = viewFor[TaskListView](Some(userAnswers))
            val applyView = view.apply(
              fakeDraftId,
              savedUntil,
              sections,
              additionalSections,
              isTaskListComplete,
              Organisation)(fakeRequest, messages)
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

          val userAnswers = emptyUserAnswers.set(AddAssetsPage, NoComplete).success.value

          for {
            sections <- sections(userAnswers)
            additionalSections <- additionalSections
            isTaskListComplete <- isTaskListComplete(userAnswers)
          } yield {
            val view = viewFor[TaskListView](Some(userAnswers))
            val applyView = view.apply(
              fakeDraftId,
              savedUntil,
              sections,
              additionalSections,
              isTaskListComplete,
              Organisation)(fakeRequest, messages)
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
          sections <- sections(emptyUserAnswers)
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete(emptyUserAnswers)
        } yield {
          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Organisation)(fakeRequest, messages)

          val doc = asDocument(applyView)
          assertRenderedById(doc, "saved-until")
        }
      }
    }

    "rendered for an Agent" must {

      "render return to saved registrations link" in {
        for {
          sections <- sections(emptyUserAnswers)
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete(emptyUserAnswers)
        } yield {
          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Agent)(fakeRequest, messages)

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
          sections <- sections(emptyUserAnswers)
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete(emptyUserAnswers)
        } yield {
          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Agent)(fakeRequest, messages)

          val doc = asDocument(applyView)

          assertAttributeValueForElement(
            doc.getElementById("agent-details"),
            "href",
            routes.AgentAnswerController.onPageLoad(fakeDraftId).url
          )
        }
      }

      "not render saved until" in {
        for {
          sections <- sections(emptyUserAnswers)
          additionalSections <- additionalSections
          isTaskListComplete <- isTaskListComplete(emptyUserAnswers)
        } yield {
          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(
            fakeDraftId,
            savedUntil,
            sections,
            additionalSections,
            isTaskListComplete,
            Agent)(fakeRequest, messages)

          val doc = asDocument(applyView)
          assertNotRenderedById(doc, "saved-until")
        }
      }
    }
  }
}
