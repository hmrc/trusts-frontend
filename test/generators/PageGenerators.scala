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

package generators

import org.scalacheck.Arbitrary
import pages._
import pages.property_or_land._
import pages.shares.{ShareClassPage, ShareCompanyNamePage, SharePortfolioNamePage, SharePortfolioOnStockExchangePage, SharePortfolioQuantityInTrustPage, SharePortfolioValueInTrustPage, ShareQuantityInTrustPage, ShareValueInTrustPage, SharesInAPortfolioPage, SharesOnStockExchangePage}

trait PageGenerators {

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
    Arbitrary(DeclarationPage)

  implicit lazy val arbitraryWhatTypeOfBeneficiaryPage: Arbitrary[WhatTypeOfBeneficiaryPage.type] =
    Arbitrary(WhatTypeOfBeneficiaryPage)

  implicit lazy val arbitraryAgentInternationalAddressPage: Arbitrary[AgentInternationalAddressPage.type] =
    Arbitrary(AgentInternationalAddressPage)

  implicit lazy val arbitraryClassBeneficiaryDescriptionPage: Arbitrary[ClassBeneficiaryDescriptionPage] =
    Arbitrary(ClassBeneficiaryDescriptionPage(0))

  implicit lazy val arbitraryAgentUKAddressPage: Arbitrary[AgentUKAddressPage.type] =
    Arbitrary(AgentUKAddressPage)

  implicit lazy val arbitraryAgentAddressYesNoPage: Arbitrary[AgentAddressYesNoPage.type] =
    Arbitrary(AgentAddressYesNoPage)

  implicit lazy val arbitraryIndividualBeneficiaryAddressUKYesNoPage: Arbitrary[IndividualBeneficiaryAddressUKYesNoPage] =
    Arbitrary(IndividualBeneficiaryAddressUKYesNoPage(0))

  implicit lazy val arbitraryAgentNamePage: Arbitrary[AgentNamePage.type] =
    Arbitrary(AgentNamePage)

  implicit lazy val arbitraryAddABeneficiaryPage: Arbitrary[AddABeneficiaryPage.type] =
    Arbitrary(AddABeneficiaryPage)

  implicit lazy val arbitraryIndividualBeneficiaryVulnerableYesNoPage: Arbitrary[IndividualBeneficiaryVulnerableYesNoPage] =
    Arbitrary(IndividualBeneficiaryVulnerableYesNoPage(0))

  implicit lazy val arbitraryIndividualBeneficiaryAddressUKPage: Arbitrary[IndividualBeneficiaryAddressUKPage] =
    Arbitrary(IndividualBeneficiaryAddressUKPage(0))

  implicit lazy val arbitraryIndividualBeneficiaryAddressYesNoPage: Arbitrary[IndividualBeneficiaryAddressYesNoPage] =
    Arbitrary(IndividualBeneficiaryAddressYesNoPage(0))

  implicit lazy val arbitraryIndividualBeneficiaryNationalInsuranceNumberPage: Arbitrary[IndividualBeneficiaryNationalInsuranceNumberPage] =
    Arbitrary(IndividualBeneficiaryNationalInsuranceNumberPage(0))

  implicit lazy val arbitraryIndividualBeneficiaryNationalInsuranceYesNoPage: Arbitrary[IndividualBeneficiaryNationalInsuranceYesNoPage] =
    Arbitrary(IndividualBeneficiaryNationalInsuranceYesNoPage(0))

  implicit lazy val arbitraryIndividualBeneficiaryIncomePage: Arbitrary[IndividualBeneficiaryIncomePage] =
    Arbitrary(IndividualBeneficiaryIncomePage(0))

  implicit lazy val arbitraryIndividualBeneficiaryIncomeYesNoPage: Arbitrary[IndividualBeneficiaryIncomeYesNoPage] =
    Arbitrary(IndividualBeneficiaryIncomeYesNoPage(0))

  implicit lazy val arbitraryIndividualBeneficiaryDateOfBirthPage: Arbitrary[IndividualBeneficiaryDateOfBirthPage] =
    Arbitrary(IndividualBeneficiaryDateOfBirthPage(0))

  implicit lazy val arbitraryIndividualBeneficiaryDateOfBirthYesNoPage: Arbitrary[IndividualBeneficiaryDateOfBirthYesNoPage] =
    Arbitrary(IndividualBeneficiaryDateOfBirthYesNoPage(0))

  implicit lazy val arbitraryIndividualBeneficiaryNamePage: Arbitrary[IndividualBeneficiaryNamePage] =
    Arbitrary(IndividualBeneficiaryNamePage(0))

  implicit lazy val arbitraryWasSettlorsAddressUKYesNoPage: Arbitrary[WasSettlorsAddressUKYesNoPage.type] =
    Arbitrary(WasSettlorsAddressUKYesNoPage)

  implicit lazy val arbitrarySetupAfterSettlorDiedPage: Arbitrary[SetupAfterSettlorDiedPage.type] =
    Arbitrary(SetupAfterSettlorDiedPage)

  implicit lazy val arbitrarySettlorsUKAddressPage: Arbitrary[SettlorsUKAddressPage.type] =
    Arbitrary(SettlorsUKAddressPage)

  implicit lazy val arbitrarySettlorsNINoYesNoPage: Arbitrary[SettlorsNINoYesNoPage.type] =
    Arbitrary(SettlorsNINoYesNoPage)

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

  implicit lazy val arbitraryAssetMoneyValuePage: Arbitrary[AssetMoneyValuePage] =
    Arbitrary(AssetMoneyValuePage(0))

  implicit lazy val arbitraryAgentInternalReferencePage: Arbitrary[AgentInternalReferencePage.type] =
    Arbitrary(AgentInternalReferencePage)

  implicit lazy val arbitraryWhatKindOfAssetPage: Arbitrary[WhatKindOfAssetPage] =
    Arbitrary(WhatKindOfAssetPage(0))

  implicit lazy val arbitraryAgentTelephoneNumberPage: Arbitrary[AgentTelephoneNumberPage.type] =
    Arbitrary(AgentTelephoneNumberPage)

  implicit lazy val arbitraryTrusteesNinoPage: Arbitrary[TrusteesNinoPage] =
    Arbitrary(TrusteesNinoPage(0))

  implicit lazy val arbitraryTrusteeLiveInTheUKPage: Arbitrary[TrusteeLiveInTheUKPage] =
    Arbitrary(TrusteeLiveInTheUKPage(0))

  implicit lazy val arbitraryTrusteesUkAddressPage: Arbitrary[TrusteesUkAddressPage] =
    Arbitrary(TrusteesUkAddressPage(0))

  implicit lazy val arbitrarytrusteeAUKCitizenPage: Arbitrary[TrusteeAUKCitizenPage] =
    Arbitrary(TrusteeAUKCitizenPage(0))

  implicit lazy val arbitraryTrusteesDateOfBirthPage: Arbitrary[TrusteesDateOfBirthPage] =
    Arbitrary(TrusteesDateOfBirthPage(0))

  implicit lazy val arbitraryTelephoneNumberPage: Arbitrary[TelephoneNumberPage] =
    Arbitrary(TelephoneNumberPage(0))

  implicit lazy val arbitraryAddATrusteePage: Arbitrary[AddATrusteePage.type] =
    Arbitrary(AddATrusteePage)

  implicit lazy val arbitraryTrusteesNamePage: Arbitrary[TrusteesNamePage] =
    Arbitrary(TrusteesNamePage(0))

  implicit lazy val arbitraryTrusteeOrIndividualPage: Arbitrary[TrusteeIndividualOrBusinessPage] =
    Arbitrary(TrusteeIndividualOrBusinessPage(0))

  implicit lazy val arbitraryIsThisLeadTrusteePage: Arbitrary[IsThisLeadTrusteePage] =
    Arbitrary(IsThisLeadTrusteePage(0))

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

  implicit lazy val arbitraryTrustResidentInUKPage: Arbitrary[TrustResidentInUKPage.type] =
    Arbitrary(TrustResidentInUKPage)

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
