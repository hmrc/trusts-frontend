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
import models.{AddABeneficiary, AddATrustee, AddAssets, FullName, IndividualOrBusiness, InternationalAddress, NonResidentType, NormalMode, UKAddress, UserAnswers, WhatKindOfAsset}
import navigation.TaskListNavigator
import pages._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import viewmodels.{Completed, InProgress}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.format(dateFormatter)

  private def sections(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).sections(answers)

  private def trustDetailsSectionStatus(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).trustDetailsSectionStatus(answers)

  private def beneficiariesSectionStatus(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).beneficiariesSectionStatus(answers)

  private def trusteesSectionStatus(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).trusteesSectionStatus(answers)

  private def settlorsSectionStatus(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).settlorsSectionStatus(answers)

  private def assetsSectionStatus(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).assetsSectionStatus(answers)

  private def isTaskListComplete(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).isTaskListComplete(answers)

  "TaskList view" when {

    "rendered for an Organisation or an Agent" must {

      "render sections" must {

        val answers = emptyUserAnswers

        val view = viewFor[TaskListView](Some(answers))

        val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)

        behave like normalPage(applyView, "taskList")

        behave like pageWithBackLink(applyView)

        behave like taskList(applyView, sections(answers))
      }

      "render correct trust details section tag" must {

        "render no tag when not started (no trust details added)" in {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)
          trustDetailsSectionStatus(emptyUserAnswers) mustEqual None
          assertNotRenderedById(doc, "task-list__task--trustDetails__tag")

        }

        "render in progress tag when trust name has been entered" in {

          val answers = emptyUserAnswers
            .set(TrustNamePage, "myTrust").success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trustDetailsSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--trustDetails__tag")

        }

        "render complete tag when when trust resident in the UK" in {

          val answers = emptyUserAnswers
            .set(TrustNamePage, "myTrust").success.value
            .set(TrustResidentOffshorePage, false).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trustDetailsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--trustDetails__tag")

        }

        "render complete tag when nonUK country trust previously resident" in {

          val answers = emptyUserAnswers
            .set(TrustNamePage, "myTrust").success.value
            .set(TrustResidentOffshorePage, true).success.value
            .set(TrustPreviouslyResidentPage, "Spain").success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trustDetailsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--trustDetails__tag")

        }

        "render complete tag when non resident type selected" in {

          val answers = emptyUserAnswers
            .set(TrustNamePage, "myTrust").success.value
            .set(NonResidentTypePage, NonResidentType.Domiciled).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trustDetailsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--trustDetails__tag")

        }

        "render complete tag when not registering for purpose of Inheritance Tax Act 1984" in {

          val answers = emptyUserAnswers
            .set(TrustNamePage, "myTrust").success.value
            .set(InheritanceTaxActPage, false).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trustDetailsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--trustDetails__tag")

        }

        "render complete tag when agent other than barrister" in {

          val answers = emptyUserAnswers
            .set(TrustNamePage, "myTrust").success.value
            .set(AgentOtherThanBarristerPage, true).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trustDetailsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--trustDetails__tag")

        }

      }

      "render correct beneficiary section tag" must {

        "render no tag when not started (no beneficiaries added)" in {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)
          beneficiariesSectionStatus(emptyUserAnswers) mustEqual None
          assertNotRenderedById(doc, "task-list__task--beneficiaries__tag")

        }

        "render in progress tag when at least one beneficiary in progress" in {

          val answers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          beneficiariesSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--beneficiaries__tag")

        }


        "render in progress tag when at least one beneficiaries in progress and 'add later' selected" in {

          val answers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.YesLater).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          beneficiariesSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--beneficiaries__tag")

        }

        "render in progress tag when at least one beneficiaries in progress and 'add now' selected" in {

          val answers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.YesNow).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          beneficiariesSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--beneficiaries__tag")

        }

        "render complete tag when at none in progress, at least one completed and 'no more to add' selected" in {

          val answers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
            .set(IndividualBeneficiaryVulnerableYesNoPage(0), true).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          beneficiariesSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--beneficiaries__tag")

        }

      }

      "render correct trustee section tag" must {

        "render no tag when not started (no trustees added)" in {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)
          trusteesSectionStatus(emptyUserAnswers) mustEqual None
          assertNotRenderedById(doc, "task-list__task--trustees__tag")

        }

        "render in progress tag when at least one trustees in progress" in {

          val answers = emptyUserAnswers.set(TrusteesNamePage(0), FullName("individual",None, "trustee")).success.value
          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trusteesSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--trustees__tag")

        }

        "render in progress tag when at least one trustee in progress and 'add later' selected" in {

          val answers = emptyUserAnswers.set(TrusteesNamePage(0), FullName("individual",None, "trustee")).success.value
            .set(AddATrusteePage, AddATrustee.YesLater).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trusteesSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--trustees__tag")

        }

        "render in progress tag when at least one trustee in progress and 'add now' selected" in {

          val answers = emptyUserAnswers.set(TrusteesNamePage(0), FullName("individual",None, "trustee")).success.value
            .set(AddATrusteePage, AddATrustee.YesNow).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trusteesSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--trustees__tag")

        }

        "render complete tag when at none in progress, has a completed lead trsutee and 'no more to add' selected" in {

          val answers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteesNamePage(0), FullName("lead",None, "trustee")).success.value
            .set(TelephoneNumberPage(0), "+11112222").success.value
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          trusteesSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--trustees__tag")

        }

      }

      "render correct settlor section tag" must {

        "render no tag when not started (no settlor added)" in {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)
          settlorsSectionStatus(emptyUserAnswers) mustEqual None
          assertNotRenderedById(doc, "task-list__task--settlors__tag")

        }

        "render in progress tag when trust setup after settlor died is selected" in {

          val answers = emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          settlorsSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--settlors__tag")

        }

        "render complete tag when deceased settlor last known address not known" in {

          val answers = emptyUserAnswers
            .set(SettlorsLastKnownAddressYesNoPage, false).success.value
            .set(SetupAfterSettlorDiedPage, true).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          settlorsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--settlors__tag")

        }

        "render complete tag when deceased settlor Nino entered" in {

          val answers = emptyUserAnswers
            .set(SettlorNationalInsuranceNumberPage, "NH111111A").success.value
            .set(SetupAfterSettlorDiedPage, true).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          settlorsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--settlors__tag")

        }

        "render complete tag deceased settlor UK adress entered" in {

          val answers = emptyUserAnswers
            .set(SettlorsUKAddressPage,  UKAddress("line 1", Some("line 2"), Some("line 3"), "line 4","line 5")).success.value
            .set(SetupAfterSettlorDiedPage, true).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          settlorsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--settlors__tag")

        }

        "render complete tag deceased settlor International adress entered" in {

          val answers = emptyUserAnswers
            .set(SettlorsInternationalAddressPage,  InternationalAddress("line 1", "line 2", None, None, "country")).success.value
            .set(SetupAfterSettlorDiedPage, true).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          settlorsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--settlors__tag")

        }

      }


      "render correct assets section tag" must {

        "render no tag when not started (no assets added)" in {

          val view = viewFor[TaskListView](Some(emptyUserAnswers))
          val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)
          assetsSectionStatus(emptyUserAnswers) mustEqual None
          assertNotRenderedById(doc, "task-list__task--assets__tag")

        }

        "render in progress tag when at least one asset in progress" in {

          val answers = emptyUserAnswers.set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assetsSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--assets__tag")

        }


        "render in progress tag when at least one asset in progress and 'add later' selected" in {

          val answers = emptyUserAnswers.set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(AddAssetsPage, AddAssets.YesLater).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assetsSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--assets__tag")

        }

        "render in progress tag when at least one asset in progress and 'add now' selected" in {

          val answers = emptyUserAnswers.set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(AddAssetsPage, AddAssets.YesNow).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assetsSectionStatus(answers) mustEqual Some(InProgress)
          assertRenderedById(doc, "task-list__task--assets__tag")

        }

        "render complete tag when at none in progress, at least one completed and 'no more to add' selected" in {

          val answers = emptyUserAnswers.set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(AssetMoneyValuePage(0), "100").success.value
            .set(AddAssetsPage, AddAssets.NoComplete).success.value

          val view = viewFor[TaskListView](Some(answers))
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assetsSectionStatus(answers) mustEqual Some(Completed)
          assertRenderedById(doc, "task-list__task--assets__tag")

        }

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
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
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
          val applyView = view.apply(savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertNotRenderedById(doc, "summaryHeading")
          assertNotRenderedById(doc, "printSaveAnswers")

        }

      }

    }

    "rendered for an Organisation" must {

      "render Saved Until" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Organisation)(fakeRequest, messages)

        val doc = asDocument(applyView)
        assertRenderedById(doc, "saved-until")
      }

    }

    "rendered for an Agent" must {

      "render return to saved registrations link" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertAttributeValueForElement(
          doc.getElementById("saved-registrations"),
          "href",
          routes.AgentOverviewController.onPageLoad().url
        )
      }

      "render agent details link" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))

        val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertAttributeValueForElement(
          doc.getElementById("agent-details"),
          "href",
          routes.AgentInternalReferenceController.onPageLoad(NormalMode).url
        )
      }

      "not render saved until" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))
        val applyView = view.apply(savedUntil, sections(emptyUserAnswers), isTaskListComplete(emptyUserAnswers), Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)
        assertNotRenderedById(doc, "saved-until")
      }

    }

  }

}
