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

package mapping.registration

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.TypeOfTrust.{EmployeeRelated, WillTrustOrIntestacyTrust}
import mapping.{registration, _}
import models.registration.pages.DeedOfVariation
import models.registration.pages.KindOfTrust.Employees
import models.registration.pages.NonResidentType.Domiciled
import models.registration.pages.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.settlors.living_settlor.trust_type.{EfrbsStartDatePage, EfrbsYesNoPage, HowDeedOfVariationCreatedPage, KindOfTrustPage, SetUpInAdditionToWillTrustYesNoPage}
import pages.register.trust_details.{AgentOtherThanBarristerPage, _}
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

      "must able to create TrustDetails for" - {

        "a UK resident trust" in {
          val date = LocalDate.now

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
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )

        }

        "an Employment Related trust with an Employer financed RBS" in {
          val date = LocalDate.now
          val index = 0
          val userAnswers =
            emptyUserAnswers
              .set(TrustNamePage, "New Trust").success.value
              .set(WhenTrustSetupPage, date).success.value
              .set(GovernedInsideTheUKPage, true).success.value
              .set(AdministrationInsideUKPage, true).success.value
              .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
              .set(EstablishedUnderScotsLawPage, true).success.value
              .set(TrustResidentOffshorePage, false).success.value
              .set(KindOfTrustPage, Employees).success.value
              .set(EfrbsYesNoPage, true).success.value
              .set(EfrbsStartDatePage, date).success.value

          val uaWithSettlor = TestUserAnswers.withIndividualLivingSettlor(index, userAnswers)

          trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
            startDate = date,
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = EmployeeRelated,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = Some(date)
          )

        }
        "a UK resident trust with trust previously resident offshore " in {
          val date = LocalDate.now

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
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )

        }

        "a non-UK governed, non-UK admin, for schedule 5A, resident type domiciled" in {
          val date = LocalDate.now

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
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )

        }

        "a non-UK governed, UK admin, not for schedule 5A, Inheritance tax act 1984" in {
          val date = LocalDate.now

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
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )

        }

        "a non-UK governed, UK admin, not for schedule 5A, no Inheritance tax act 1984" in {
          val date = LocalDate.now

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
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )

        }

        "a UK resident with mixed trustees when a settlor is based in the UK" in {
          val date = LocalDate.now

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
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )

        }

        "a non-UK resident with mixed trustees when all settlors are international" in {
          val date = LocalDate.now

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
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )

        }

        "a deed of variation for" - {

          "an answer taken from DeedOfVariation" in {
            val date = LocalDate.now

            val userAnswers =
              emptyUserAnswers
                .set(TrustNamePage, "New Trust").success.value
                .set(WhenTrustSetupPage, date).success.value
                .set(GovernedInsideTheUKPage, true).success.value
                .set(AdministrationInsideUKPage, true).success.value
                .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
                .set(EstablishedUnderScotsLawPage, true).success.value
                .set(TrustResidentOffshorePage, false).success.value
                .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value

            val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

            trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
              startDate = date,
              lawCountry = None,
              administrationCountry = None,
              residentialStatus = None,
              typeOfTrust = WillTrustOrIntestacyTrust,
              deedOfVariation = Some(DeedOfVariation.ReplacedWill),
              interVivos = None,
              efrbsStartDate = None
            )

          }

          "an answer taken from AdditionToAWillTrust" in {
            val date = LocalDate.now

            val userAnswers =
              emptyUserAnswers
                .set(TrustNamePage, "New Trust").success.value
                .set(WhenTrustSetupPage, date).success.value
                .set(GovernedInsideTheUKPage, true).success.value
                .set(AdministrationInsideUKPage, true).success.value
                .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
                .set(EstablishedUnderScotsLawPage, true).success.value
                .set(TrustResidentOffshorePage, false).success.value
                .set(SetUpInAdditionToWillTrustYesNoPage, true).success.value

            val uaWithSettlor = TestUserAnswers.withDeceasedSettlor(userAnswers)

            trustDetailsMapper.build(uaWithSettlor).value mustBe registration.TrustDetailsType(
              startDate = date,
              lawCountry = None,
              administrationCountry = None,
              residentialStatus = None,
              typeOfTrust = WillTrustOrIntestacyTrust,
              deedOfVariation = Some(DeedOfVariation.AdditionToWill),
              interVivos = None,
              efrbsStartDate = None
            )

          }

        }

      }

    }

  }

}
