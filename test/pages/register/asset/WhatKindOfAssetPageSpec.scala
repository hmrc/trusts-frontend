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

package pages.register.asset

import java.time.LocalDate

import models.core.UserAnswers
import models.core.pages.UKAddress
import models.registration.pages.Status.Completed
import models.registration.pages.{ShareClass, Status, WhatKindOfAsset}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus.AssetStatus
import pages.register.asset.business._
import pages.register.asset.money._
import pages.register.asset.other._
import pages.register.asset.partnership._
import pages.register.asset.property_or_land._
import pages.register.asset.shares._

class WhatKindOfAssetPageSpec extends PageBehaviours {

  "WhatKindOfAssetPage" must {

    beRetrievable[WhatKindOfAsset](WhatKindOfAssetPage(0))

    beSettable[WhatKindOfAsset](WhatKindOfAssetPage(0))

    beRemovable[WhatKindOfAsset](WhatKindOfAssetPage(0))
  }

  "remove money when changing type of asset" in {

    val kindOfAsset = arbitrary[WhatKindOfAsset] suchThat (x => x != WhatKindOfAsset.Money)

    forAll(arbitrary[UserAnswers], kindOfAsset) {
      (initial, kind) =>
        val answers: UserAnswers = initial
          .set(AssetMoneyValuePage(0), "200").success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), kind).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual kind
        result.get(AssetMoneyValuePage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

  "remove share portfolio data when changing type of asset" in {

    val kindOfAsset = arbitrary[WhatKindOfAsset] suchThat (x => x != WhatKindOfAsset.Shares)

    forAll(arbitrary[UserAnswers], kindOfAsset) {
      (initial, kind) =>
        val answers: UserAnswers = initial
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(SharePortfolioNamePage(0), "Shares").success.value
          .set(SharePortfolioOnStockExchangePage(0), true).success.value
          .set(SharePortfolioQuantityInTrustPage(0), "20").success.value
          .set(SharePortfolioValueInTrustPage(0), "2000").success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), kind).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual kind

        result.get(SharesInAPortfolioPage(0)) mustNot be(defined)
        result.get(SharePortfolioNamePage(0)) mustNot be(defined)
        result.get(SharePortfolioOnStockExchangePage(0)) mustNot be(defined)
        result.get(SharePortfolioQuantityInTrustPage(0)) mustNot be(defined)
        result.get(SharePortfolioValueInTrustPage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

  "remove share data when changing type of asset" in {
    val kindOfAsset = arbitrary[WhatKindOfAsset] suchThat (x => x != WhatKindOfAsset.Shares)

    forAll(arbitrary[UserAnswers], kindOfAsset) {
      (initial, kind) =>

        val answers: UserAnswers = initial
          .set(SharesInAPortfolioPage(0), false).success.value
          .set(ShareCompanyNamePage(0), "Company").success.value
          .set(SharesOnStockExchangePage(0), false).success.value
          .set(ShareClassPage(0), ShareClass.Ordinary).success.value
          .set(ShareQuantityInTrustPage(0), "20").success.value
          .set(ShareValueInTrustPage(0), "2000").success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), kind).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual kind

        result.get(SharesInAPortfolioPage(0)) mustNot be(defined)
        result.get(ShareCompanyNamePage(0)) mustNot be(defined)
        result.get(SharesOnStockExchangePage(0)) mustNot be(defined)
        result.get(ShareClassPage(0)) mustNot be(defined)
        result.get(ShareQuantityInTrustPage(0)) mustNot be(defined)
        result.get(ShareValueInTrustPage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

  "remove property or land when changing type of asset" in {
    val kindOfAsset = arbitrary[WhatKindOfAsset] suchThat (x => x != WhatKindOfAsset.PropertyOrLand)

    forAll(arbitrary[UserAnswers], kindOfAsset) {
      (initial, kind) =>

        val answers: UserAnswers = initial
          .set(PropertyOrLandAddressYesNoPage(0), true).success.value
          .set(PropertyOrLandAddressUkYesNoPage(0), true).success.value
          .set(PropertyOrLandUKAddressPage(0), UKAddress(
            "line 1",
            "line 2",
            None,
            None,
            "NE1 1NN"
          )).success.value
          .set(PropertyOrLandTotalValuePage(0), "10,000").success.value
          .set(TrustOwnAllThePropertyOrLandPage(0), true).success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), kind).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual kind

        result.get(PropertyOrLandAddressYesNoPage(0)) mustNot be(defined)
        result.get(PropertyOrLandAddressUkYesNoPage(0)) mustNot be(defined)
        result.get(PropertyOrLandUKAddressPage(0)) mustNot be(defined)
        result.get(PropertyOrLandTotalValuePage(0)) mustNot be(defined)
        result.get(TrustOwnAllThePropertyOrLandPage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

  "remove partnership when changing type of asset" in {

    val kindOfAsset = arbitrary[WhatKindOfAsset] suchThat (x => x != WhatKindOfAsset.Partnership)

    forAll(arbitrary[UserAnswers], kindOfAsset) {
      (initial, kind) =>
        val answers: UserAnswers = initial
          .set(PartnershipDescriptionPage(0), "Partnership Description 1").success.value
          .set(PartnershipStartDatePage(0), LocalDate.now).success.value
          .set(AssetStatus(0), Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), kind).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual kind
        result.get(PartnershipDescriptionPage(0)) mustNot be(defined)
        result.get(PartnershipStartDatePage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

  "remove other when changing type of asset" in {

    val kindOfAsset = arbitrary[WhatKindOfAsset] suchThat (x => x != WhatKindOfAsset.Other)

    forAll(arbitrary[UserAnswers], kindOfAsset) {
      (initial, kind) =>
        val answers: UserAnswers = initial
          .set(OtherAssetDescriptionPage(0), "Description").success.value
          .set(OtherAssetValuePage(0), "200").success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), kind).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual kind
        result.get(OtherAssetDescriptionPage(0)) mustNot be(defined)
        result.get(OtherAssetValuePage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

  "remove business when changing type of asset" in {

    val kindOfAsset = arbitrary[WhatKindOfAsset] suchThat (x => x != WhatKindOfAsset.Business)

    forAll(arbitrary[UserAnswers], kindOfAsset) {
      (initial, kind) =>
        val answers: UserAnswers = initial
          .set(BusinessNamePage(0), "Name").success.value
          .set(BusinessDescriptionPage(0), "Description").success.value
          .set(BusinessAddressUkYesNoPage(0), true).success.value
          .set(BusinessUkAddressPage(0), UKAddress("Line 1", "Line 2", None, None, "POSTCODE")).success.value
          .set(BusinessValuePage(0), "400").success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), kind).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual kind
        result.get(BusinessNamePage(0)) mustNot be(defined)
        result.get(BusinessDescriptionPage(0)) mustNot be(defined)
        result.get(BusinessAddressUkYesNoPage(0)) mustNot be(defined)
        result.get(BusinessUkAddressPage(0)) mustNot be(defined)
        result.get(BusinessValuePage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

}
