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
import models.entities.Trustees
import models.{FullName, NormalMode, UserAnswers}
import pages._
import pages.entitystatus.TrustDetailsStatus
import viewmodels.{Beneficiaries, Settlors, TaxLiability, TrustDetails}

class TaskListNavigatorSpec extends SpecBase {

  val navigator : TaskListNavigator = new TaskListNavigator

  "TaskList Navigator" must {

    "for trust details task" when {

      "trust details has been answered" must {

        "go to Check Trust Answers Page" in {
          val answers = UserAnswers(userAnswersId)
            .set(TrustNamePage, "Trust of John").success.value
              .set(WhenTrustSetupPage, LocalDate.of(2010,10,10)).success.value
              .set(TrustDetailsStatus, Completed).success.value
          navigator.nextPage(TrustDetails, answers) mustBe routes.TrustDetailsAnswerPageController.onPageLoad()
        }

      }

      "trust details has not been answered" must {

        "go to TrustName Page" in {
          navigator.nextPage(TrustDetails, emptyUserAnswers) mustBe routes.TrustNameController.onPageLoad(NormalMode)
        }

      }

    }

    "for settlors task" when {

      "there are no settlors" must {

        "go to SetupAfterSettlorDiedPage" in {
          navigator.nextPage(Settlors, emptyUserAnswers) mustBe routes.SetupAfterSettlorDiedController.onPageLoad(NormalMode)
        }

      }


      "there are settlors" must {

        "go to DeceasedSettlorAnswerPage" in {
          val answers = UserAnswers(userAnswersId).set(SetupAfterSettlorDiedPage, true).success.value
              .set(SettlorsNamePage, FullName("deceased",None, "settlor")).success.value
              .set(DeceasedSettlorComplete, Completed).success.value
          navigator.nextPage(Settlors, answers) mustBe routes.DeceasedSettlorAnswerController.onPageLoad()
        }

      }

    }

    "for beneficiaries task" when {

      "there are no beneficiaries" must {

        "go to BeneficiaryInfoPage" in {
          navigator.nextPage(Beneficiaries, emptyUserAnswers) mustBe routes.IndividualBeneficiaryInfoController.onPageLoad()
        }

      }


      "there are individual beneficiaries" must {

        "go to AddABeneficiary" in {
          val answers = UserAnswers(userAnswersId)
            .set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
          navigator.nextPage(Beneficiaries, answers) mustBe routes.AddABeneficiaryController.onPageLoad()
        }

      }

      "there are class of beneficiaries" must {

        "go to AddABeneficiary" in {
          val answers = UserAnswers(userAnswersId)
            .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
          navigator.nextPage(Beneficiaries, answers) mustBe routes.AddABeneficiaryController.onPageLoad()
        }

      }

    }

    "for assets task" when {

      "there are no assets" must {

        "go to AssetInfoPage" in {
          navigator.nextPage(Assets, emptyUserAnswers) mustBe routes.AssetInterruptPageController.onPageLoad()
        }

      }


      "there are assets" must {

        "go to AddAAsset" in {
          val answers = UserAnswers(userAnswersId)
            .set(WhatKindOfAssetPage(0), Money).success.value
          navigator.nextPage(Assets, answers) mustBe routes.AddAssetsController.onPageLoad()
        }

      }

    }

    "for trustee task" when {

      "there are no trustees" must {

        "go to TrusteeInfoPage" in {
          navigator.nextPage(Trustees, emptyUserAnswers) mustBe routes.TrusteesInfoController.onPageLoad()
        }

      }

      "there are trustees" must {

        "go to AddATrustee" in {
          val answers = UserAnswers(userAnswersId)
            .set(IsThisLeadTrusteePage(0), false).success.value

          navigator.nextPage(Trustees, answers) mustBe routes.AddATrusteeController.onPageLoad()
        }

      }

    }

    "for task liability task" must {

        "go to TaxLiabilityPage" in {
          navigator.nextPage(TaxLiability, emptyUserAnswers) mustBe routes.TaskListController.onPageLoad()
        }

      }

    }

}
