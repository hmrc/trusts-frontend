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
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.business.SettlorBusinessNamePage
import pages.register.settlors.deceased_settlor.{RemoveSettlorPage => RemoveDeceasedSettlorPage, _}
import pages.register.settlors.living_settlor.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage}
import pages.register.settlors.{AddASettlorPage, SetUpAfterSettlorDiedYesNoPage}
import pages.register.trust_details._

trait PageGenerators {

  implicit lazy val arbitraryPartnershipStartDatePage: Arbitrary[PartnershipStartDatePage] =
    Arbitrary(PartnershipStartDatePage(0))

  implicit lazy val arbitraryPartnershipDescriptionPage: Arbitrary[PartnershipDescriptionPage] =
    Arbitrary(PartnershipDescriptionPage(0))

  implicit lazy val arbitrarySettlorBusinessDetailsPage: Arbitrary[SettlorBusinessNamePage] =
    Arbitrary(SettlorBusinessNamePage(0))

  implicit lazy val arbitraryKindOfTrustPage: Arbitrary[KindOfTrustPage.type] =
    Arbitrary(KindOfTrustPage)


  implicit lazy val arbitrarySettlorsBasedInTheUKPage: Arbitrary[SettlorsBasedInTheUKPage.type] =
    Arbitrary(SettlorsBasedInTheUKPage)

  implicit lazy val arbitraryTrusteesBasedInTheUKPage: Arbitrary[TrusteesBasedInTheUKPage.type] =
    Arbitrary(TrusteesBasedInTheUKPage)

  implicit lazy val arbitraryHoldoverReliefYesNoPage: Arbitrary[HoldoverReliefYesNoPage.type] =
    Arbitrary(HoldoverReliefYesNoPage)

  implicit lazy val arbitraryRemoveSettlorPage: Arbitrary[RemoveSettlorPage] =
    Arbitrary(RemoveSettlorPage(0))

  implicit lazy val arbitrarySettlorIndividualPassportYesNoPage: Arbitrary[SettlorIndividualPassportYesNoPage] =
    Arbitrary(SettlorIndividualPassportYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualPassportPage: Arbitrary[SettlorIndividualPassportPage] =
    Arbitrary(SettlorIndividualPassportPage(0))

  implicit lazy val arbitrarySettlorIndividualIDCardYesNoPage: Arbitrary[SettlorIndividualIDCardYesNoPage] =
    Arbitrary(SettlorIndividualIDCardYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualIDCardPage: Arbitrary[SettlorIndividualIDCardPage] =
    Arbitrary(SettlorIndividualIDCardPage(0))

  implicit lazy val arbitrarySettlorIndividualAddressUKYesNoPage: Arbitrary[SettlorAddressUKYesNoPage] =
    Arbitrary(SettlorAddressUKYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualAddressUKPage: Arbitrary[SettlorAddressUKPage] =
    Arbitrary(SettlorAddressUKPage(0))

  implicit lazy val arbitrarySettlorIndividualAddressInternationalPage: Arbitrary[SettlorAddressInternationalPage] =
    Arbitrary(SettlorAddressInternationalPage(0))

  implicit lazy val arbitrarySettlorIndividualNINOYesNoPage: Arbitrary[SettlorIndividualNINOYesNoPage] =
    Arbitrary(SettlorIndividualNINOYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualNINOPage: Arbitrary[SettlorIndividualNINOPage] =
    Arbitrary(SettlorIndividualNINOPage(0))

  implicit lazy val arbitrarySettlorIndividualAddressYesNoPage: Arbitrary[SettlorAddressYesNoPage] =
    Arbitrary(SettlorAddressYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualDateOfBirthPage: Arbitrary[SettlorIndividualDateOfBirthPage] =
    Arbitrary(SettlorIndividualDateOfBirthPage(0))

  implicit lazy val arbitrarySettlorIndividualDateOfBirthYesNoPage: Arbitrary[SettlorIndividualDateOfBirthYesNoPage] =
    Arbitrary(SettlorIndividualDateOfBirthYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualNamePage: Arbitrary[SettlorIndividualNamePage] =
    Arbitrary(SettlorIndividualNamePage(0))

  implicit lazy val arbitrarySettlorIndividualOrBusinessPage: Arbitrary[SettlorIndividualOrBusinessPage] =
    Arbitrary(SettlorIndividualOrBusinessPage(0))

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

  implicit lazy val arbitraryWasSettlorsAddressUKYesNoPage: Arbitrary[WasSettlorsAddressUKYesNoPage.type] =
    Arbitrary(WasSettlorsAddressUKYesNoPage)

  implicit lazy val arbitrarySetUpAfterSettlorDiedPage: Arbitrary[SetUpAfterSettlorDiedYesNoPage.type] =
    Arbitrary(SetUpAfterSettlorDiedYesNoPage)

  implicit lazy val arbitrarySettlorsUKAddressPage: Arbitrary[SettlorsUKAddressPage.type] =
    Arbitrary(SettlorsUKAddressPage)

  implicit lazy val arbitrarySettlorsNINoYesNoPage: Arbitrary[SettlorsNationalInsuranceYesNoPage.type] =
    Arbitrary(SettlorsNationalInsuranceYesNoPage)

  implicit lazy val arbitrarySettlorsNamePage: Arbitrary[SettlorsNamePage.type] =
    Arbitrary(SettlorsNamePage)

  implicit lazy val arbitrarySettlorsLastKnownAddressYesNoPage: Arbitrary[SettlorsLastKnownAddressYesNoPage.type] =
    Arbitrary(SettlorsLastKnownAddressYesNoPage)

  implicit lazy val arbitrarySettlorsInternationalAddressPage: Arbitrary[SettlorsInternationalAddressPage.type] =
    Arbitrary(SettlorsInternationalAddressPage)

  implicit lazy val arbitrarySettlorsDateOfBirthPage: Arbitrary[SettlorsDateOfBirthPage.type] =
    Arbitrary(SettlorsDateOfBirthPage)

  implicit lazy val arbitrarySettlorNationalInsuranceNumberPage: Arbitrary[SettlorNationalInsuranceNumberPage.type] =
    Arbitrary(SettlorNationalInsuranceNumberPage)

  implicit lazy val arbitrarySettlorDateOfDeathYesNoPage: Arbitrary[SettlorDateOfDeathYesNoPage.type] =
    Arbitrary(SettlorDateOfDeathYesNoPage)

  implicit lazy val arbitrarySettlorDateOfDeathPage: Arbitrary[SettlorDateOfDeathPage.type] =
    Arbitrary(SettlorDateOfDeathPage)

  implicit lazy val arbitrarySettlorDateOfBirthYesNoPage: Arbitrary[SettlorDateOfBirthYesNoPage.type] =
    Arbitrary(SettlorDateOfBirthYesNoPage)

  implicit lazy val arbitraryAddAssetsPage: Arbitrary[AddAssetsPage.type] =
    Arbitrary(AddAssetsPage)

  implicit lazy val arbitraryAddASettlorPage: Arbitrary[AddASettlorPage.type] =
    Arbitrary(AddASettlorPage)

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

  implicit lazy val arbitraryWhenTrustSetupPage: Arbitrary[WhenTrustSetupPage.type] =
    Arbitrary(WhenTrustSetupPage)

  implicit lazy val arbitraryAgentOtherThanBarristerPage: Arbitrary[AgentOtherThanBarristerPage.type] =
    Arbitrary(AgentOtherThanBarristerPage)

  implicit lazy val arbitraryInheritanceTaxActPage: Arbitrary[InheritanceTaxActPage.type] =
    Arbitrary(InheritanceTaxActPage)

  implicit lazy val arbitraryNonResidentTypePage: Arbitrary[NonResidentTypePage.type] =
    Arbitrary(NonResidentTypePage)

  implicit lazy val arbitraryTrustPreviouslyResidentPage: Arbitrary[TrustPreviouslyResidentPage.type] =
    Arbitrary(TrustPreviouslyResidentPage)

  implicit lazy val arbitraryTrustResidentOffshorePage: Arbitrary[TrustResidentOffshorePage.type] =
    Arbitrary(TrustResidentOffshorePage)

  implicit lazy val arbitraryRegisteringTrustFor5APage: Arbitrary[RegisteringTrustFor5APage.type] =
    Arbitrary(RegisteringTrustFor5APage)

  implicit lazy val arbitraryEstablishedUnderScotsLawPage: Arbitrary[EstablishedUnderScotsLawPage.type] =
    Arbitrary(EstablishedUnderScotsLawPage)

  implicit lazy val arbitraryCountryAdministeringTrustPage: Arbitrary[CountryAdministeringTrustPage.type] =
    Arbitrary(CountryAdministeringTrustPage)

  implicit lazy val arbitraryAdministrationInsideUKPage: Arbitrary[AdministrationInsideUKPage.type] =
    Arbitrary(AdministrationInsideUKPage)

  implicit lazy val arbitraryCountryGoverningTrustPage: Arbitrary[CountryGoverningTrustPage.type] =
    Arbitrary(CountryGoverningTrustPage)

  implicit lazy val arbitraryGovernedInsideTheUKPage: Arbitrary[GovernedInsideTheUKPage.type] =
    Arbitrary(GovernedInsideTheUKPage)

  implicit lazy val arbitraryTrustNamePage: Arbitrary[TrustNamePage.type] =
    Arbitrary(TrustNamePage)
}
