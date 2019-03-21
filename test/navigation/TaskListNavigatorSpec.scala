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
import models.{NormalMode, UserAnswers}
import pages._

class TaskListNavigatorSpec extends SpecBase {

  val navigator : TaskListNavigator = new TaskListNavigator

  "TaskList Navigator" must {

    "for trust details task" when {

      "trust details has been answered" must {

        "go to Check Trust Answers Page" in {
          val answers = UserAnswers(userAnswersId)
            .set(TrustNamePage, "Trust of John").success.value
              .set(WhenTrustSetupPage, LocalDate.of(2010,10,10)).success.value

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

        "go to SettlorInfoPage" in {
          navigator.nextPage(Settlors, emptyUserAnswers) mustBe routes.TaskListController.onPageLoad()
        }

      }


      "there are settlors" must {

        "go to AddASettlor" in {
          val answers = emptyUserAnswers
          navigator.nextPage(Settlors, answers) mustBe routes.TaskListController.onPageLoad()
        }

      }

    }

    "for beneficiaries task" when {

      "there are no beneficiaries" must {

        "go to BeneficiaryInfoPage" in {
          navigator.nextPage(Beneficiaries, emptyUserAnswers) mustBe routes.TaskListController.onPageLoad()
        }

      }


      "there are beneficiaries" must {

        "go to AddABeneficiary" in {
          val answers = emptyUserAnswers
          navigator.nextPage(Beneficiaries, answers) mustBe routes.TaskListController.onPageLoad()
        }

      }

    }

    "for assets task" when {

      "there are no assets" must {

        "go to AssetInfoPage" in {
          navigator.nextPage(Assets, emptyUserAnswers) mustBe routes.TaskListController.onPageLoad()
        }

      }


      "there are assets" must {

        "go to AddAAsset" in {
          val answers = emptyUserAnswers
          navigator.nextPage(Assets, answers) mustBe routes.TaskListController.onPageLoad()
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
            .set(IsThisLeadTrusteePage(0), true).success.value

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
