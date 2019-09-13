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

package navigation

import java.time.LocalDate

import base.SpecBase
import controllers.routes
import models.Status.Completed
import models.WhatKindOfAsset.Money
import mapping.reads.{Assets, Trustees}
import models.{FullName, NormalMode, UserAnswers}
import pages._
import pages.deceased_settlor.SettlorsNamePage
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import pages.trustees.IsThisLeadTrusteePage
import sections.{Beneficiaries, Settlors, TaxLiability, TrustDetails}

class TaskListNavigatorSpec extends SpecBase {

  val navigator : TaskListNavigator = new TaskListNavigator

  "TaskList Navigator" must {

    "for trust details task" when {

      "trust details has been answered" must {

        "go to Check Trust Answers Page" in {
          val answers = emptyUserAnswers
            .set(TrustNamePage, "Trust of John").success.value
              .set(WhenTrustSetupPage, LocalDate.of(2010,10,10)).success.value
              .set(TrustDetailsStatus, Completed).success.value
          navigator.nextPage(TrustDetails, answers, fakeDraftId) mustBe routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId)
        }

      }

      "trust details has not been answered" must {

        "go to TrustName Page" in {
          navigator.nextPage(TrustDetails, emptyUserAnswers, fakeDraftId) mustBe routes.TrustNameController.onPageLoad(NormalMode, fakeDraftId)
        }

      }

    }

    "for settlors task" when {

      "there are no settlors" must {

        "go to SetupAfterSettlorDiedPage" in {
          navigator.nextPage(Settlors, emptyUserAnswers, fakeDraftId) mustBe routes.SetupAfterSettlorDiedController.onPageLoad(NormalMode, fakeDraftId)
        }

      }


      "there are settlors" must {

        "go to DeceasedSettlorAnswerPage" in {
          val answers = emptyUserAnswers.set(SetupAfterSettlorDiedPage, true).success.value
              .set(SettlorsNamePage, FullName("deceased",None, "settlor")).success.value
              .set(DeceasedSettlorStatus, Completed).success.value

          navigator.nextPage(Settlors, answers, fakeDraftId) mustBe controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId)
        }

      }

    }

    "for beneficiaries task" when {

      "there are no beneficiaries" must {

        "go to BeneficiaryInfoPage" in {
          navigator.nextPage(Beneficiaries, emptyUserAnswers, fakeDraftId) mustBe routes.IndividualBeneficiaryInfoController.onPageLoad(fakeDraftId)
        }

      }


      "there are individual beneficiaries" must {

        "go to AddABeneficiary" in {
          val answers = emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
          navigator.nextPage(Beneficiaries, answers, fakeDraftId) mustBe routes.AddABeneficiaryController.onPageLoad(fakeDraftId)
        }

      }

      "there are class of beneficiaries" must {

        "go to AddABeneficiary" in {
          val answers = emptyUserAnswers
            .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
          navigator.nextPage(Beneficiaries, answers, fakeDraftId) mustBe routes.AddABeneficiaryController.onPageLoad(fakeDraftId)
        }

      }

    }

    "for assets task" when {

      "there are no assets" must {

        "go to AssetInfoPage" in {
          navigator.nextPage(Assets, emptyUserAnswers, fakeDraftId) mustBe routes.AssetInterruptPageController.onPageLoad(fakeDraftId)
        }

      }


      "there are assets" must {

        "go to AddAAsset" in {
          val answers = emptyUserAnswers
            .set(WhatKindOfAssetPage(0), Money).success.value
          navigator.nextPage(Assets, answers, fakeDraftId) mustBe routes.AddAssetsController.onPageLoad(fakeDraftId)
        }

      }

    }

    "for trustee task" when {

      "there are no trustees" must {

        "go to TrusteeInfoPage" in {
          navigator.nextPage(Trustees, emptyUserAnswers, fakeDraftId) mustBe controllers.trustees.routes.TrusteesInfoController.onPageLoad(fakeDraftId)
        }

      }

      "there are trustees" must {

        "go to AddATrustee" in {
          val answers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), false).success.value

          navigator.nextPage(Trustees, answers, fakeDraftId) mustBe controllers.trustees.routes.AddATrusteeController.onPageLoad(fakeDraftId)
        }

      }

    }

    "for task liability task" must {

        "go to TaxLiabilityPage" in {
          navigator.nextPage(TaxLiability, emptyUserAnswers, fakeDraftId) mustBe routes.TaskListController.onPageLoad(fakeDraftId)
        }

      }

    }

}
