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
import models.core.pages.FullName
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages._
import navigation.registration.TaskListNavigator
import pages._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.beneficiaries.individual.IndividualBeneficiaryNamePage
import pages.register.beneficiaries.{AddABeneficiaryPage, ClassBeneficiaryDescriptionPage}
import pages.entitystatus._
import pages.register.RegistrationProgress
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.{RegistrationProgress, WhenTrustSetupPage}
import pages.register.trustees.{AddATrusteePage, IsThisLeadTrusteePage}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.register.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.format(dateFormatter)
  private def sections(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).items(answers,fakeDraftId)

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
            .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
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
          assertRenderedById(doc, "summary-paragraph")
          assertRenderedById(doc, "print-and-save")

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
          assertNotRenderedById(doc, "print-and-save")

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
          routes.AgentAnswerController.onPageLoad(fakeDraftId).url
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
