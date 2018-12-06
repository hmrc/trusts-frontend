/*
 * Copyright 2018 HM Revenue & Customs
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

  implicit lazy val arbitraryRegisteringTrustFor5APage: Arbitrary[RegisteringTrustFor5APage.type] =
    Arbitrary(RegisteringTrustFor5APage)

  implicit lazy val arbitraryEstablishedUnderScotsLawPage: Arbitrary[EstablishedUnderScotsLawPage.type] =
    Arbitrary(EstablishedUnderScotsLawPage)

  implicit lazy val arbitraryTrustResidentInUKPage: Arbitrary[TrustResidentInUKPage.type] =
    Arbitrary(TrustResidentInUKPage)

  implicit lazy val arbitraryCountryAdministeringTrustPage: Arbitrary[CountryAdministeringTrustPage.type] =
    Arbitrary(CountryAdministeringTrustPage)

  implicit lazy val arbitraryAdministrationOutsideUKPage: Arbitrary[AdministrationOutsideUKPage.type] =
    Arbitrary(AdministrationOutsideUKPage)

  implicit lazy val arbitraryCountryGoverningTrustPage: Arbitrary[CountryGoverningTrustPage.type] =
    Arbitrary(CountryGoverningTrustPage)

  implicit lazy val arbitraryGovernedOutsideTheUKPage: Arbitrary[GovernedOutsideTheUKPage.type] =
    Arbitrary(GovernedOutsideTheUKPage)

  implicit lazy val arbitraryTrustNamePage: Arbitrary[TrustNamePage.type] =
    Arbitrary(TrustNamePage)
}
