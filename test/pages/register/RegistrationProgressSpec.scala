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

package pages.register

import java.time.LocalDate

import base.SpecBase
import models.core.UserAnswers
import models.core.pages.FullName
import models.core.pages.IndividualOrBusiness.Individual
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.{Completed, InProgress}
import models.registration.pages._
import pages.entitystatus._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.shares._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import pages.register.beneficiaries.individual.IndividualBeneficiaryNamePage
import pages.register.beneficiaries.{AddABeneficiaryPage, ClassBeneficiaryDescriptionPage}
import pages.register.settlors.AddASettlorPage
import pages.register.settlors.deceased_settlor.SetupAfterSettlorDiedPage
import pages.register.settlors.living_settlor.SettlorIndividualOrBusinessPage
import pages.register.trustees.{AddATrusteePage, IsThisLeadTrusteePage}
import play.api.libs.json.{JsObject, Json}

class RegistrationProgressSpec extends SpecBase {

  "Trust details section" must {

    "render no tag" when {

      "no status value in user answers" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers

        registrationProgress.isTrustDetailsComplete(userAnswers) mustBe None
      }

    }

    "render in-progress tag" when {

      "user has entered when the trust was created" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value

        registrationProgress.isTrustDetailsComplete(userAnswers).value mustBe InProgress
      }

    }

    "render complete tag" when {

      "user answer has reached check-trust-details" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
          .set(TrustDetailsStatus, Completed).success.value

        registrationProgress.isTrustDetailsComplete(userAnswers).value mustBe Completed
      }

    }

  }

  "Trustee section" must {

    "render no tag" when {

      "no trustees in user answers" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers

        registrationProgress.isTrusteesComplete(userAnswers) mustBe None
      }

      "trustees list is empty" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val json = Json.parse(
          """
            |{
            |        "trustees" : [],
            |        "addATrustee" : "no-complete"
            |}
            |""".stripMargin)

        val userAnswers = UserAnswers(draftId = fakeDraftId, data = json.as[JsObject], internalAuthId = "id")

        registrationProgress.isTrusteesComplete(userAnswers) mustBe None
      }

    }

    "render in-progress tag" when {

      "there are trustees that are incomplete" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(0), true).success.value
          .set(IsThisLeadTrusteePage(1), false).success.value
          .set(TrusteeStatus(1), Status.Completed).success.value

        registrationProgress.isTrusteesComplete(userAnswers).value mustBe InProgress
      }

      "there are trustees that are complete, but section flagged not complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(0), true).success.value
          .set(TrusteeStatus(0), Status.Completed).success.value
          .set(IsThisLeadTrusteePage(1), false).success.value
          .set(TrusteeStatus(1), Status.Completed).success.value
          .set(AddATrusteePage, AddATrustee.YesLater).success.value

        registrationProgress.isTrusteesComplete(userAnswers).value mustBe InProgress
      }

      "there are completed trustees, the section is flagged as completed, but there is no lead trustee" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(0), false).success.value
          .set(TrusteeStatus(0), Status.Completed).success.value
          .set(IsThisLeadTrusteePage(1), false).success.value
          .set(TrusteeStatus(1), Status.Completed).success.value
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        registrationProgress.isTrusteesComplete(userAnswers).value mustBe InProgress
      }

    }

    "render complete tag" when {

      "there are trustees that are complete, and section flagged as complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(0), true).success.value
          .set(TrusteeStatus(0), Status.Completed).success.value
          .set(IsThisLeadTrusteePage(1), false).success.value
          .set(TrusteeStatus(1), Status.Completed).success.value
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        registrationProgress.isTrusteesComplete(userAnswers).value mustBe Completed
      }

    }

  }

  "Settlor section" must {

    "render no tag" when {

      "no settlors in user answers" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers

        registrationProgress.isSettlorsComplete(userAnswers) mustBe None
      }

    }

    "render in-progress tag" when {

      "there is a deceased settlor that is not completed" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(DeceasedSettlorStatus, Status.InProgress).success.value

        registrationProgress.isSettlorsComplete(userAnswers).value mustBe InProgress
      }

      "there are no living settlors added and the trust was not setup after the settlor died" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, false).success.value

        registrationProgress.isSettlorsComplete(userAnswers).value mustBe InProgress
      }

      "there are living settlors that are not completed" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, false).success.value
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(SettlorIndividualOrBusinessPage(1), Individual).success.value
          .set(LivingSettlorStatus(1), Status.Completed).success.value

        registrationProgress.isSettlorsComplete(userAnswers).value mustBe InProgress
      }


      "there are living settlors that are all complete, but user answered AddMore" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, false).success.value
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(LivingSettlorStatus(0), Status.Completed).success.value
          .set(AddASettlorPage, AddASettlor.YesLater).success.value

        registrationProgress.isSettlorsComplete(userAnswers).value mustBe InProgress
      }

    }

    "render complete tag" when {

      "there is a deceased settlor marked as complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, true).success.value
          .set(DeceasedSettlorStatus, Status.Completed).success.value

        registrationProgress.isSettlorsComplete(userAnswers).value mustBe Completed
      }

      "there are living settlors marked as complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, false).success.value
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(LivingSettlorStatus(0), Status.Completed).success.value
          .set(AddASettlorPage, AddASettlor.NoComplete).success.value

        registrationProgress.isSettlorsComplete(userAnswers).value mustBe Completed
      }


    }

  }

  "Beneficiary section" must {

      "render no tag" when {

        "there is no beneficiaries in user answers" in {
          val registrationProgress = injector.instanceOf[RegistrationProgress]

          val userAnswers = emptyUserAnswers

          registrationProgress.isBeneficiariesComplete(userAnswers) mustBe None
        }

        "individual beneficiaries list is empty" in {
          val registrationProgress = injector.instanceOf[RegistrationProgress]

          val json = Json.parse(
            """
              |{
              |"beneficiaries" : {
              |            "whatTypeOfBeneficiary" : "Individual",
              |            "individualBeneficiaries" : [
              |            ]
              |        }
              |}
              |""".stripMargin)

          val userAnswers = UserAnswers(draftId = fakeDraftId, data = json.as[JsObject], internalAuthId = "id")

          registrationProgress.isBeneficiariesComplete(userAnswers) mustBe None
        }

        "class of beneficiaries list is empty" in {
          val registrationProgress = injector.instanceOf[RegistrationProgress]

          val json = Json.parse(
            """
              |{
              |"beneficiaries" : {
              |            "whatTypeOfBeneficiary" : "Individual",
              |            "classOfBeneficiaries" : [
              |            ]
              |        }
              |}
              |""".stripMargin)

          val userAnswers = UserAnswers(draftId = fakeDraftId, data = json.as[JsObject], internalAuthId = "id")

          registrationProgress.isBeneficiariesComplete(userAnswers) mustBe None
        }

      }

    "render in-progress tag" when {

      "there are individual beneficiaries only that are in progress" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value

        registrationProgress.isBeneficiariesComplete(userAnswers).value mustBe InProgress
      }

      "there are beneficiaries that are incomplete" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "Description").success.value
          .set(ClassBeneficiaryStatus(0), Status.Completed).success.value
          .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value

        registrationProgress.isBeneficiariesComplete(userAnswers).value mustBe InProgress
      }

      "there are benficiaries that are all complete, but user answered AddMore" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "Description").success.value
          .set(ClassBeneficiaryStatus(0), Status.Completed).success.value
          .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
          .set(IndividualBeneficiaryStatus(0), Status.Completed).success.value
          .set(AddABeneficiaryPage, AddABeneficiary.YesLater).success.value

        registrationProgress.isBeneficiariesComplete(userAnswers).value mustBe InProgress
      }

    }

    "render complete tag" when {

      "there are individual beneficiaries only that are complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
          .set(IndividualBeneficiaryStatus(0), Completed).success.value
          .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

        registrationProgress.isBeneficiariesComplete(userAnswers).value mustBe Completed
      }

      "there are beneficiaries marked as complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "Description").success.value
          .set(ClassBeneficiaryStatus(0), Status.Completed).success.value
          .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
          .set(IndividualBeneficiaryStatus(0), Status.Completed).success.value
          .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

        registrationProgress.isBeneficiariesComplete(userAnswers).value mustBe Completed
      }

    }

  }

  "Assets section" must {

    "render no tag" when {

      "there are no assets in user answers" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers

        registrationProgress.assetsStatus(userAnswers) mustBe None
      }

      "assets list is empty" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val json = Json.parse(
          """
            |{
            |   "assets" : []
            |}
            |""".stripMargin)

        val userAnswers = UserAnswers(draftId = fakeDraftId, data = json.as[JsObject], internalAuthId = "id")

        registrationProgress.assetsStatus(userAnswers) mustBe None
      }

    }

    "render in-progress tag" when {

      "there are assets in user answers that are not complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value

        registrationProgress.assetsStatus(userAnswers).value mustBe InProgress
      }

    }

    "render complete tag" when {

      "there are assets in user answers that are complete" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(0), "2000").success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(1), true).success.value
          .set(SharePortfolioNamePage(1), "Portfolio").success.value
          .set(SharePortfolioQuantityInTrustPage(1), "30").success.value
          .set(SharePortfolioValueInTrustPage(1), "999999999999").success.value
          .set(SharePortfolioOnStockExchangePage(1), false).success.value
          .set(AssetStatus(1), Completed).success.value
          .set(AddAssetsPage, NoComplete).success.value

        registrationProgress.assetsStatus(userAnswers).value mustBe Completed
      }

    }

  }

  "All tasklist complete" when {

    "all entities marked as complete" in {

      val registrationProgress = injector.instanceOf[RegistrationProgress]

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
        .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
        .set(SharesInAPortfolioPage(1), true).success.value
        .set(SharePortfolioNamePage(1), "Portfolio").success.value
        .set(SharePortfolioQuantityInTrustPage(1), "30").success.value
        .set(SharePortfolioValueInTrustPage(1), "999999999999").success.value
        .set(SharePortfolioOnStockExchangePage(1), false).success.value
        .set(AssetStatus(1), Completed).success.value
        .set(AddAssetsPage, NoComplete).success.value

      registrationProgress.isTaskListComplete(userAnswers) mustBe true
    }


  }


}
