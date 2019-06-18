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

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import controllers.routes
import models.AddAssets.NoComplete
import models.Status.Completed
import models.{AddABeneficiary, AddATrustee, FullName, NormalMode, Status, UserAnswers, WhatKindOfAsset}
import navigation.TaskListNavigator
import pages.entitystatus._
import pages._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.format(dateFormatter)
  private def sections(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).sections(answers,fakeDraftId)

  private def isTaskListComplete(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).isTaskListComplete(answers)

  "TaskList view" when {

    "rendered for an Organisation or an Agent" must {

      "render sections" must {

        val answers = emptyUserAnswers

        val view = viewFor[TaskListView](Some(answers))

        val applyView = view.apply(fakeDraftId, savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)

        behave like normalPage(applyView, "taskList")

        behave like pageWithBackLink(applyView)

        behave like taskList(applyView, sections(answers))
      }

      "render summary" when {

        "all sections are completed" in {

          val userAnswers = emptyUserAnswers
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(TrustDetailsStatus, Completed).success.value
            .set(IsThisLeadTrusteePage(0), false).success.value
            .set(TrusteeStatus(0), Status.Completed).success.value
            .set(IsThisLeadTrusteePage(1), true).success.value
            .set(TrusteeStatus(1), Status.Completed).success.value
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(DeceasedSettlorStatus, Status.Completed).success.value
            .set(ClassBeneficiaryDescriptionPage(0), "Description").success.value
            .set(ClassBeneficiaryStatus(0), Status.Completed).success.value
            .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
            .set(IndividualBeneficiaryStatus(0), Status.Completed).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(AssetMoneyValuePage(0), "2000").success.value
            .set(AssetStatus(0), Completed).success.value
            .set(AddAssetsPage, NoComplete).success.value

          val view = viewFor[TaskListView](Some(userAnswers))
          val applyView = view.apply(fakeDraftId, savedUntil, sections(userAnswers), isTaskListComplete(userAnswers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertRenderedById(doc, "summaryHeading")
          assertRenderedById(doc, "printSaveAnswers")

        }

      }

      "not render summary" when {

        "not all sections are completed" in {

          val userAnswers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0), FullName("individual", None, "beneficiary")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

          val view = viewFor[TaskListView](Some(userAnswers))
          val applyView = view.apply(fakeDraftId, savedUntil, sections(userAnswers), isTaskListComplete(userAnswers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertNotRenderedById(doc, "summaryHeading")
          assertNotRenderedById(doc, "printSaveAnswers")

        }

      }

    }

    "rendered for an Organisation" must {

      "render Saved Until" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(fakeDraftId, savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Organisation)(fakeRequest, messages)

        val doc = asDocument(applyView)
        assertRenderedById(doc, "saved-until")
      }

    }

    "rendered for an Agent" must {

      "render return to saved registrations link" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(fakeDraftId, savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertAttributeValueForElement(
          doc.getElementById("saved-registrations"),
          "href",
          routes.AgentOverviewController.onPageLoad().url
        )
      }

      "render agent details link" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))

        val applyView = view.apply(fakeDraftId, savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertAttributeValueForElement(
          doc.getElementById("agent-details"),
          "href",
          routes.AgentInternalReferenceController.onPageLoad(NormalMode, fakeDraftId).url
        )
      }

      "not render saved until" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(fakeDraftId, savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)
        assertNotRenderedById(doc, "saved-until")
      }

    }

  }

}
