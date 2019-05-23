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
import models.{AddABeneficiary, AddATrustee, AddAssets, FullName, NormalMode, UserAnswers, WhatKindOfAsset}
import navigation.TaskListNavigator
import pages.RegistrationProgress
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.TaskListView
import pages._

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.format(dateFormatter)
  private val trustName: String = "Trust Name"

  private val registrationProgress = new RegistrationProgress(new TaskListNavigator())

  private def sections(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).sections(answers)

  private def isTaskListComplete(answers: UserAnswers) =
    registrationProgress.isTaskListComplete(answers)

  "TaskList view" when {

    "rendered for an Organisation or an Agent" must {

      "render sections" must {

        val answers = emptyUserAnswers

        val view = viewFor[TaskListView](Some(answers))

        val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), trustName, Organisation)(fakeRequest, messages)

        behave like normalPage(applyView, "taskList")

        behave like pageWithBackLink(applyView)

        behave like taskList(applyView, sections(answers))
      }

      "render summary" when {

        "all sections are completed" in {

          val answers = emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
            .set(IndividualBeneficiaryVulnerableYesNoPage(0), true).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteesNamePage(0), FullName("lead",None, "trustee")).success.value
            .set(TelephoneNumberPage(0), "+11112222").success.value
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(SettlorsLastKnownAddressYesNoPage, false).success.value
            .set(TrustNamePage, "myTrust").success.value
            .set(TrustResidentOffshorePage, false).success.value
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(AssetMoneyValuePage(0), "100").success.value
            .set(AddAssetsPage, AddAssets.NoComplete).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), trustName, Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertRenderedById(doc, "summaryHeading")
          assertRenderedById(doc, "printSaveAnswers")

        }

      }

      "not render summary" when {

        "not all sections are completed" in {

          val answers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), false, trustName, Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertNotRenderedById(doc, "summaryHeading")
          assertNotRenderedById(doc, "printSaveAnswers")

        }

      }

    }

    "rendered for an Organisation" must {

      "render Saved Until" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), trustName, Organisation)(fakeRequest, messages)

        val doc = asDocument(applyView)
        assertRenderedById(doc, "saved-until")
      }

    }

    "rendered for an Agent" must {

      "render return to saved registrations link" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), trustName, Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertAttributeValueForElement(
          doc.getElementById("saved-registrations"),
          "href",
          routes.AgentOverviewController.onPageLoad().url
        )
      }

      "render agent details link" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))

        val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), trustName, Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertAttributeValueForElement(
          doc.getElementById("agent-details"),
          "href",
          routes.AgentInternalReferenceController.onPageLoad(NormalMode).url
        )
      }

      "not render saved until" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), trustName, Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)
        assertNotRenderedById(doc, "saved-until")
      }

    }

  }

}
