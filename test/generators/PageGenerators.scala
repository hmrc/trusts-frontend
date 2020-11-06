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

package generators

import org.scalacheck.Arbitrary
import pages.register
import pages.register._
import pages.register.agents._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.partnership._
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.asset.{AddAnAssetYesNoPage, AddAssetsPage, WhatKindOfAssetPage}

trait PageGenerators {

  implicit lazy val arbitraryPartnershipStartDatePage: Arbitrary[PartnershipStartDatePage] =
    Arbitrary(PartnershipStartDatePage(0))

  implicit lazy val arbitraryPartnershipDescriptionPage: Arbitrary[PartnershipDescriptionPage] =
    Arbitrary(PartnershipDescriptionPage(0))

  implicit lazy val arbitraryAddAnAssetYesNoPage: Arbitrary[AddAnAssetYesNoPage.type] =
    Arbitrary(AddAnAssetYesNoPage)

  implicit lazy val arbitraryPropertyOrLandAddressYesNoPage: Arbitrary[PropertyOrLandAddressYesNoPage] =
    Arbitrary(PropertyOrLandAddressYesNoPage(0))

  implicit lazy val arbitraryPropertyLandValueTrustPage: Arbitrary[PropertyLandValueTrustPage] =
    Arbitrary(PropertyLandValueTrustPage(0))

  implicit lazy val arbitraryPropertyOrLandAddressPage: Arbitrary[PropertyOrLandAddressUkYesNoPage] =
    Arbitrary(PropertyOrLandAddressUkYesNoPage(0))

  implicit lazy val arbitraryPropertyOrLandTotalValuePage: Arbitrary[PropertyOrLandTotalValuePage] =
    Arbitrary(PropertyOrLandTotalValuePage(0))

  implicit lazy val arbitraryPropertyOrLandDescriptionPage: Arbitrary[PropertyOrLandDescriptionPage] =
    Arbitrary(PropertyOrLandDescriptionPage(0))

  implicit lazy val arbitraryTrustOwnAllThePropertyOrLandPage: Arbitrary[TrustOwnAllThePropertyOrLandPage] =
    Arbitrary(TrustOwnAllThePropertyOrLandPage(0))

  implicit lazy val arbitraryPropertyOrLandInternationalAddressPage: Arbitrary[PropertyOrLandInternationalAddressPage] =
    Arbitrary(PropertyOrLandInternationalAddressPage(0))

  implicit lazy val arbitraryPropertyOrLandAddressUkYesNoPage: Arbitrary[PropertyOrLandUKAddressPage] =
    Arbitrary(PropertyOrLandUKAddressPage(0))

  implicit lazy val arbitraryShareCompanyNamePage: Arbitrary[ShareCompanyNamePage] =
    Arbitrary(ShareCompanyNamePage(0))

  implicit lazy val arbitrarySharesOnStockExchangePage: Arbitrary[SharesOnStockExchangePage] =
    Arbitrary(SharesOnStockExchangePage(0))

  implicit lazy val arbitrarySharesInAPortfolioPage: Arbitrary[SharesInAPortfolioPage] =
    Arbitrary(SharesInAPortfolioPage(0))

  implicit lazy val arbitraryShareValueInTrustPage: Arbitrary[ShareValueInTrustPage] =
    Arbitrary(ShareValueInTrustPage(0))

  implicit lazy val arbitraryShareQuantityInTrustPage: Arbitrary[ShareQuantityInTrustPage] =
    Arbitrary(ShareQuantityInTrustPage(0))

  implicit lazy val arbitrarySharePortfolioValueInTrustPage: Arbitrary[SharePortfolioValueInTrustPage] =
    Arbitrary(SharePortfolioValueInTrustPage(0))

  implicit lazy val arbitrarySharePortfolioQuantityInTrustPage: Arbitrary[SharePortfolioQuantityInTrustPage] =
    Arbitrary(SharePortfolioQuantityInTrustPage(0))

  implicit lazy val arbitrarySharePortfolioOnStockExchangePage: Arbitrary[SharePortfolioOnStockExchangePage] =
    Arbitrary(SharePortfolioOnStockExchangePage(0))

  implicit lazy val arbitrarySharePortfolioNamePage: Arbitrary[SharePortfolioNamePage] =
    Arbitrary(SharePortfolioNamePage(0))

  implicit lazy val arbitraryShareClassPage: Arbitrary[ShareClassPage] =
    Arbitrary(ShareClassPage(0))

  implicit lazy val arbitraryDeclarationPage: Arbitrary[DeclarationPage.type] =
    Arbitrary(register.DeclarationPage)

  implicit lazy val arbitraryAgentInternationalAddressPage: Arbitrary[AgentInternationalAddressPage.type] =
    Arbitrary(AgentInternationalAddressPage)

  implicit lazy val arbitraryAgentUKAddressPage: Arbitrary[AgentUKAddressPage.type] =
    Arbitrary(AgentUKAddressPage)

  implicit lazy val arbitraryAgentAddressYesNoPage: Arbitrary[AgentAddressYesNoPage.type] =
    Arbitrary(AgentAddressYesNoPage)

  implicit lazy val arbitraryAgentNamePage: Arbitrary[AgentNamePage.type] =
    Arbitrary(AgentNamePage)

  implicit lazy val arbitraryAddAssetsPage: Arbitrary[AddAssetsPage.type] =
    Arbitrary(AddAssetsPage)

  implicit lazy val arbitraryAssetMoneyValuePage: Arbitrary[AssetMoneyValuePage] =
    Arbitrary(AssetMoneyValuePage(0))

  implicit lazy val arbitraryAgentInternalReferencePage: Arbitrary[AgentInternalReferencePage.type] =
    Arbitrary(AgentInternalReferencePage)

  implicit lazy val arbitraryWhatKindOfAssetPage: Arbitrary[WhatKindOfAssetPage] =
    Arbitrary(WhatKindOfAssetPage(0))

  implicit lazy val arbitraryAgentTelephoneNumberPage: Arbitrary[AgentTelephoneNumberPage.type] =
    Arbitrary(AgentTelephoneNumberPage)

  implicit lazy val arbitraryPostcodeForTheTrustPage: Arbitrary[PostcodeForTheTrustPage.type] =
    Arbitrary(PostcodeForTheTrustPage)

  implicit lazy val arbitraryWhatIsTheUTRPage: Arbitrary[WhatIsTheUTRPage.type] =
    Arbitrary(WhatIsTheUTRPage)

  implicit lazy val arbitraryTrustHaveAUTRPage: Arbitrary[TrustHaveAUTRPage.type] =
    Arbitrary(TrustHaveAUTRPage)

  implicit lazy val arbitraryTrustRegisteredOnlinePage: Arbitrary[TrustRegisteredOnlinePage.type] =
    Arbitrary(TrustRegisteredOnlinePage)

}
