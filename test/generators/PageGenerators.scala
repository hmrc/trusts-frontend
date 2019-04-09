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

trait PageGenerators {

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
