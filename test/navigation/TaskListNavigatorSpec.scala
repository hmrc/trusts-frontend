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

package navigation

import java.time.LocalDate

import base.RegistrationSpecBase
import models.NormalMode
import models.core.pages.FullName
import models.core.pages.IndividualOrBusiness.Individual
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset.Money
import navigation.registration.TaskListNavigator
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import pages.register.asset.WhatKindOfAssetPage
import pages.register.beneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual.IndividualBeneficiaryNamePage
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor.SettlorsNamePage
import pages.register.settlors.living_settlor.{SettlorIndividualNamePage, SettlorIndividualOrBusinessPage}
import pages.register.trust_details.{TrustNamePage, WhenTrustSetupPage}
import pages.register.trustees.IsThisLeadTrusteePage
import controllers.register.routes._

class TaskListNavigatorSpec extends RegistrationSpecBase {

  val navigator : TaskListNavigator = new TaskListNavigator

  "TaskList Navigator" must {

    "for trust details task" when {

      "trust details has been answered" must {

        "go to Check Trust Answers Page" in {
          val answers = emptyUserAnswers
            .set(TrustNamePage, "Trust of John").success.value
              .set(WhenTrustSetupPage, LocalDate.of(2010,10,10)).success.value
              .set(TrustDetailsStatus, Completed).success.value
          navigator.trustDetailsJourney(answers, fakeDraftId) mustBe controllers.register.trust_details.routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId)
        }

      }

      "trust details has not been answered" must {

        "go to TrustName Page" in {
          navigator.trustDetailsJourney(emptyUserAnswers, fakeDraftId) mustBe controllers.register.trust_details.routes.TrustNameController.onPageLoad(NormalMode, fakeDraftId)
        }

      }

    }

    "for settlors task" when {

      "there are no settlors" must {

        "go to SettlorInfo" in {
          navigator.settlorsJourney(emptyUserAnswers, fakeDraftId) mustBe controllers.register.settlors.routes.SettlorInfoController.onPageLoad(fakeDraftId)
        }

      }

      "there is a deceased settlor" must {

        "go to DeceasedSettlorAnswerPage" in {
          val answers = emptyUserAnswers.set(SetUpAfterSettlorDiedYesNoPage, true).success.value
              .set(SettlorsNamePage, FullName("deceased",None, "settlor")).success.value
              .set(DeceasedSettlorStatus, Completed).success.value

          navigator.settlorsJourney(answers, fakeDraftId) mustBe controllers.register.settlors.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId)
        }

        "go to SetUpAfterSettlorDied when deceased settlor is not complete" in {
          val answers = emptyUserAnswers.set(SetUpAfterSettlorDiedYesNoPage, true).success.value
            .set(SettlorsNamePage, FullName("deceased",None, "settlor")).success.value
         navigator.settlorsJourney(answers, fakeDraftId) mustBe controllers.register.settlors.routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode,fakeDraftId)
        }

      }

    }

    "there is a deceased settlor" must {

      "go to DeceasedSettlorAnswerPage" in {
        val answers = emptyUserAnswers.set(SetUpAfterSettlorDiedYesNoPage, true).success.value
          .set(SettlorsNamePage, FullName("deceased",None, "settlor")).success.value
          .set(DeceasedSettlorStatus, Completed).success.value

        navigator.settlorsJourney(answers, fakeDraftId) mustBe controllers.register.settlors.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId)
      }

      "go to SetUpAfterSettlorDied when deceased settlor is not complete" in {
        val answers = emptyUserAnswers.set(SetUpAfterSettlorDiedYesNoPage, true).success.value
          .set(SettlorsNamePage, FullName("deceased",None, "settlor")).success.value
        navigator.settlorsJourney(answers, fakeDraftId) mustBe controllers.register.settlors.routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode,fakeDraftId)
      }

    }
    "there are living settlors" must {

      "go to AddASettlor" in {
        val answers = emptyUserAnswers.set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(SettlorIndividualNamePage(0), FullName("living settlor",None, "settlor")).success.value

        navigator.settlorsJourney(answers, fakeDraftId) mustBe controllers.register.settlors.routes.AddASettlorController.onPageLoad(fakeDraftId)
      }
    }

  }


    "for beneficiaries task" when {

      "there are no beneficiaries" must {

        "go to BeneficiaryInfoPage" in {
          navigator.beneficiariesJourney(emptyUserAnswers, fakeDraftId) mustBe controllers.register.beneficiaries.routes.IndividualBeneficiaryInfoController.onPageLoad(fakeDraftId)
        }

      }


      "there are individual beneficiaries" must {

        "go to AddABeneficiary" in {
          val answers = emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(0), FullName("individual",None, "beneficiary")).success.value
          navigator.beneficiariesJourney(answers, fakeDraftId) mustBe controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(fakeDraftId)
        }

      }

      "there are class of beneficiaries" must {

        "go to AddABeneficiary" in {
          val answers = emptyUserAnswers
            .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
          navigator.beneficiariesJourney(answers, fakeDraftId) mustBe controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(fakeDraftId)
        }

      }

    }

    "for assets task" when {

      "there are no assets" must {

        "go to AssetInfoPage" in {
          navigator.assetsJourney(emptyUserAnswers, fakeDraftId) mustBe controllers.register.asset.routes.AssetInterruptPageController.onPageLoad(fakeDraftId)
        }

      }


      "there are assets" must {

        "go to AddAAsset" in {
          val answers = emptyUserAnswers
            .set(WhatKindOfAssetPage(0), Money).success.value
          navigator.assetsJourney(answers, fakeDraftId) mustBe controllers.register.asset.routes.AddAssetsController.onPageLoad(fakeDraftId)
        }

      }

    }

    "for trustee task" when {

      "there are no trustees" must {

        "go to TrusteeInfoPage" in {
          navigator.trusteesJourney(emptyUserAnswers, fakeDraftId) mustBe controllers.register.trustees.routes.TrusteesInfoController.onPageLoad(fakeDraftId)
        }

      }

      "there are trustees" must {

        "go to AddATrustee" in {
          val answers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), false).success.value

          navigator.trusteesJourney(answers, fakeDraftId) mustBe controllers.register.trustees.routes.AddATrusteeController.onPageLoad(fakeDraftId)
        }

      }

    }

    "for task liability task" must {

        "go to TaxLiabilityPage" in {
          navigator.taxLiabilityJourney(fakeDraftId) mustBe TaskListController.onPageLoad(fakeDraftId)
        }

      }

}
