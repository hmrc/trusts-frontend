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

package mapping.registration

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.TypeOfTrust.WillTrustOrIntestacyTrust
import mapping.{registration, _}
import models.registration.pages.NonResidentType.Domiciled
import models.registration.pages.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import models.registration.pages.WhenTrustSetupPage
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._
import pages.register.{AdministrationInsideUKPage, CountryAdministeringTrustPage, CountryGoverningTrustPage, EstablishedUnderScotsLawPage, GovernedInsideTheUKPage, InheritanceTaxActPage, NonResidentTypePage, RegisteringTrustFor5APage, TrustNamePage, TrustPreviouslyResidentPage, TrustResidentOffshorePage}
import pages.register.agents.AgentOtherThanBarristerPage
import pages.register.settlors.SettlorsBasedInTheUKPage
import pages.register.trustees.TrusteesBasedInTheUKPage
import utils.TestUserAnswers

class TrustDetailsMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  lazy val trustDetailsMapper : Mapping[TrustDetailsType] = injector.instanceOf[TrustDetailsMapper]

  "TrustDetailsMapper" - {

    "when user answers is empty" - {

      "must not be able to create TrustDetails" in {

        val userAnswers = emptyUserAnswers

        trustDetailsMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty " - {

      "must able to create TrustDetails for a UK resident trust" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
            .set(EstablishedUnderScotsLawPage, true).success.value
            .set(TrustResidentOffshorePage, false).success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
          startDate = date,
          lawCountry = None,
          administrationCountry = Some("GB"),
          residentialStatus = Some(ResidentialStatusType(
            uk = Some(
              UkType(
                scottishLaw = true,
                preOffShore = None
              )
            ),
            nonUK = None
          )),
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }

      "must able to create TrustDetails for a UK resident trust with trust previously resident offshore " in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
            .set(EstablishedUnderScotsLawPage, true).success.value
            .set(TrustResidentOffshorePage, true).success.value
            .set(TrustPreviouslyResidentPage, "FR").success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
          startDate = date,
          lawCountry = None,
          administrationCountry = Some("GB"),
          residentialStatus = Some(ResidentialStatusType(
            uk = Some(
              UkType(
                scottishLaw = true,
                preOffShore = Some("FR")
              )
            ),
            nonUK = None
          )),
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }

      "must able to create TrustDetails for a non-UK governed, non-UK admin, for schedule 5A, resident type domiciled" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, false).success.value
            .set(CountryGoverningTrustPage, "FR").success.value
            .set(AdministrationInsideUKPage, false).success.value
            .set(CountryAdministeringTrustPage, "FR").success.value
            .set(TrusteesBasedInTheUKPage, NonUkBasedTrustees).success.value
            .set(RegisteringTrustFor5APage, true).success.value
            .set(NonResidentTypePage, Domiciled).success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
          startDate = date,
          lawCountry = Some("FR"),
          administrationCountry = Some("FR"),
          residentialStatus = Some(ResidentialStatusType(
            uk = None,
            nonUK = Some(NonUKType(true,None,None,Some("Non Resident Domiciled")))
          )),

          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }


      "must able to create TrustDetails for a non-UK governed, UK admin, not for schedule 5A, Inheritance tax act 1984" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, false).success.value
            .set(CountryGoverningTrustPage, "FR").success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, NonUkBasedTrustees).success.value
            .set(RegisteringTrustFor5APage, false).success.value
            .set(InheritanceTaxActPage, true).success.value
            .set(AgentOtherThanBarristerPage, true).success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
          startDate = date,
          lawCountry = Some("FR"),
          administrationCountry = Some("GB"),
          residentialStatus = Some(ResidentialStatusType(
            uk = None,
            nonUK = Some(NonUKType(false,Some(true),Some(true),None))
          )),

          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }

      "must able to create TrustDetails for a non-UK governed, UK admin, not for schedule 5A, no Inheritance tax act 1984" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, false).success.value
            .set(CountryGoverningTrustPage, "FR").success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, NonUkBasedTrustees).success.value
            .set(RegisteringTrustFor5APage, false).success.value
            .set(InheritanceTaxActPage, false).success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
          startDate = date,
          lawCountry = Some("FR"),
          administrationCountry = Some("GB"),
          residentialStatus = Some(ResidentialStatusType(
            uk = None,
            nonUK = Some(NonUKType(false,Some(false),None,None))
          )),

          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }

      "must not able  to create TrustDetails for a non-UK governed, UK admin, not for schedule 5A and no inheritance tax available" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, false).success.value
            .set(CountryGoverningTrustPage, "FR").success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, NonUkBasedTrustees).success.value
            .set(RegisteringTrustFor5APage, false).success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor) mustBe None

      }

      "must be able to create UK resident TrustDetails with mixed trustees when a settlor is based in the UK" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, false).success.value
            .set(CountryGoverningTrustPage, "GB").success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, InternationalAndUKTrustees).success.value
            .set(SettlorsBasedInTheUKPage, true).success.value
            .set(RegisteringTrustFor5APage, false).success.value
            .set(EstablishedUnderScotsLawPage, true).success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
          startDate = date,
          lawCountry = Some("GB"),
          administrationCountry = Some("GB"),
          residentialStatus = Some(ResidentialStatusType(
            uk = Some(
              UkType(
                scottishLaw = true,
                preOffShore = None
              )
            ),
            nonUK = None
          )),
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }

      "must be able to create non-UK resident TrustDetails with mixed trustees when all settlors are international" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, false).success.value
            .set(CountryGoverningTrustPage, "FR").success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, InternationalAndUKTrustees).success.value
            .set(SettlorsBasedInTheUKPage, false).success.value
            .set(RegisteringTrustFor5APage, false).success.value
            .set(InheritanceTaxActPage, false).success.value
            .set(RegisteringTrustFor5APage, false).success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
          startDate = date,
          lawCountry = Some("FR"),
          administrationCountry = Some("GB"),
          residentialStatus = Some(
            ResidentialStatusType(
            uk = None,
            nonUK = Some(NonUKType(false,Some(false),None,None))
          )),
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }

      "must not able to create TrustDetails when only trust name and setup details available" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value

        val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

        trustDetailsMapper.build(uaWithSettlor) mustBe None

      }
    }

  }

}
