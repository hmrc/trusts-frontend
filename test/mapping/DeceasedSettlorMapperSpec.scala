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

package mapping

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.TypeOfTrust.WillTrustOrIntestacyTrust
import models.{FullName, InternationalAddress, UKAddress}
import models.NonResidentType.Domiciled
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._

class DeceasedSettlorMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val deceasedSettlorMapper : Mapping[WillType] = injector.instanceOf[DeceasedSettlorMapper]

  "DeceasedSettlorMapper" - {

    "when user answers is empty" - {

      "must not be able to create Deceased Settlor" in {

        val userAnswers = emptyUserAnswers

        deceasedSettlorMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty " - {

      "must be able to create a deceased settlor with minimal data journey" in {

        val userAnswers =
          emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
            .set(SettlorDateOfDeathYesNoPage, false).success.value
            .set(SettlorDateOfBirthYesNoPage, false).success.value
            .set(SettlorsNINoYesNoPage, false).success.value
            .set(SettlorsLastKnownAddressYesNoPage, false).success.value

        deceasedSettlorMapper.build(userAnswers).value mustBe WillType(
          name = NameType("First", None, "Last"),
          dateOfBirth = None,
          dateOfDeath = None,
          identification = None
        )

      }

      "must be able to create a deceased settlor with date of death" in {

        val dateOfDeath = LocalDate.of(1994, 10, 10)
        val userAnswers =
          emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
            .set(SettlorDateOfDeathYesNoPage, true).success.value
            .set(SettlorDateOfDeathPage, dateOfDeath).success.value
            .set(SettlorDateOfBirthYesNoPage, false).success.value
            .set(SettlorsNINoYesNoPage, false).success.value
            .set(SettlorsLastKnownAddressYesNoPage, false).success.value

        deceasedSettlorMapper.build(userAnswers).value mustBe WillType(
          name = NameType("First", None, "Last"),
          dateOfBirth = None,
          dateOfDeath = Some(dateOfDeath),
          identification = None
        )

      }

      "must be able to create a deceased settlor with date of birth" in {

        val dateOfBirth = LocalDate.of(1944, 10, 10)
        val userAnswers =
          emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
            .set(SettlorDateOfDeathYesNoPage, false).success.value
            .set(SettlorDateOfBirthYesNoPage, true).success.value
            .set(SettlorsDateOfBirthPage, dateOfBirth).success.value
            .set(SettlorsNINoYesNoPage, false).success.value
            .set(SettlorsLastKnownAddressYesNoPage, false).success.value

        deceasedSettlorMapper.build(userAnswers).value mustBe WillType(
          name = NameType("First", None, "Last"),
          dateOfBirth = Some(dateOfBirth),
          dateOfDeath = None,
          identification = None
        )

      }

      "must be able to create a deceased settlor with nino" in {

        val dateOfBirth = LocalDate.of(1944, 10, 10)
        val userAnswers =
          emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
            .set(SettlorDateOfDeathYesNoPage, false).success.value
            .set(SettlorDateOfBirthYesNoPage, false).success.value
            .set(SettlorsNINoYesNoPage, true).success.value
            .set(SettlorNationalInsuranceNumberPage, "NH111111A").success.value
            .set(SettlorsLastKnownAddressYesNoPage, false).success.value

        deceasedSettlorMapper.build(userAnswers).value mustBe WillType(
          name = NameType("First", None, "Last"),
          dateOfBirth = None,
          dateOfDeath = None,
          identification = Some(Identification(Some("NH111111A"), None))
        )

      }

//      "must able to create TrustDetails for a UK resident trust" in {
//        val dateOfBirth = LocalDate.of(1944, 10, 10)
//        val dateOfDeath = LocalDate.of(1994, 10, 10)
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
//            .set(SettlorDateOfDeathYesNoPage, true).success.value
//            .set(SettlorDateOfDeathPage, dateOfDeath).success.value
//            .set(SettlorDateOfBirthYesNoPage, true).success.value
//            .set(SettlorsDateOfBirthPage, dateOfBirth).success.value
//            .set(SettlorsNINoYesNoPage, true).success.value
//            .set(SettlorNationalInsuranceNumberPage, "NH111111A").success.value
//            .set(SettlorsLastKnownAddressYesNoPage, true).success.value
//            .set(WasSettlorsAddressUKYesNoPage, true).success.value
//            .set(SettlorsUKAddressPage, UKAddress("line1", Some("line2"), Some("line3"), "Newcastle", "ab1 1ab")).success.value
//            .set(SettlorsInternationalAddressPage, InternationalAddress("line1","line2",Some("line3"),Some("Pune"), "IN")).success.value
//
//        deceasedSettlorMapper.build(userAnswers).value mustBe WillType(
//          name = NameType("First", None, "Last"),
//          dateOfBirth = Some(dateOfBirth),
//          dateOfDeath = Some(dateOfDeath),
//          identification = settlorIdentification
//          startDate = date,
//          lawCountry = None,
//          administrationCountry = Some("GB"),
//          residentialStatus = Some(ResidentialStatusType(
//            uk = Some(
//              UkType(
//                scottishLaw = true,
//                preOffShore = None
//              )
//            ),
//            nonUK = None
//          )),
//          typeOfTrust = WillTrustOrIntestacyTrust,
//          deedOfVariation = None,
//          interVivos = None,
//          efrbsStartDate = None
//        )
//
//      }



//      "must able to create TrustDetails for a UK resident trust with trust previously resident offshore " in {
//        val date = LocalDate.of(2010, 10, 10)
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(TrustNamePage, "New Trust").success.value
//            .set(WhenTrustSetupPage, date).success.value
//            .set(GovernedInsideTheUKPage, true).success.value
//            .set(AdministrationInsideUKPage, true).success.value
//            .set(TrustResidentInUKPage, true).success.value
//            .set(EstablishedUnderScotsLawPage, true).success.value
//            .set(TrustResidentOffshorePage, true).success.value
//            .set(TrustPreviouslyResidentPage, "FR").success.value
//
//        trustDetailsMapper.build(userAnswers).value mustBe TrustDetailsType(
//          startDate = date,
//          lawCountry = None,
//          administrationCountry = Some("GB"),
//          residentialStatus = Some(ResidentialStatusType(
//            uk = Some(
//              UkType(
//                scottishLaw = true,
//                preOffShore = Some("FR")
//              )
//            ),
//            nonUK = None
//          )),
//          typeOfTrust = WillTrustOrIntestacyTrust,
//          deedOfVariation = None,
//          interVivos = None,
//          efrbsStartDate = None
//        )
//
//      }
//
//      "must able to create TrustDetails for a non-UK governed, non-UK admin, for schedule 5A, resident type domiciled" in {
//        val date = LocalDate.of(2010, 10, 10)
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(TrustNamePage, "New Trust").success.value
//            .set(WhenTrustSetupPage, date).success.value
//            .set(GovernedInsideTheUKPage, false).success.value
//            .set(CountryGoverningTrustPage, "FR").success.value
//            .set(AdministrationInsideUKPage, false).success.value
//            .set(CountryAdministeringTrustPage, "FR").success.value
//            .set(TrustResidentInUKPage, false).success.value
//            .set(RegisteringTrustFor5APage, true).success.value
//            .set(NonResidentTypePage, Domiciled).success.value
//
//        trustDetailsMapper.build(userAnswers).value mustBe TrustDetailsType(
//          startDate = date,
//          lawCountry = Some("FR"),
//          administrationCountry = Some("FR"),
//          residentialStatus = Some(ResidentialStatusType(
//            uk = None,
//            nonUK = Some(NonUKType(true,None,None,Some("Non Resident Domiciled")))
//          )),
//
//          typeOfTrust = WillTrustOrIntestacyTrust,
//          deedOfVariation = None,
//          interVivos = None,
//          efrbsStartDate = None
//        )
//
//      }
//
//
//      "must able to create TrustDetails for a non-UK governed, UK admin, not for schedule 5A, Inheritance tax act 1984" in {
//        val date = LocalDate.of(2010, 10, 10)
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(TrustNamePage, "New Trust").success.value
//            .set(WhenTrustSetupPage, date).success.value
//            .set(GovernedInsideTheUKPage, false).success.value
//            .set(CountryGoverningTrustPage, "FR").success.value
//            .set(AdministrationInsideUKPage, true).success.value
//            .set(TrustResidentInUKPage, false).success.value
//            .set(RegisteringTrustFor5APage, false).success.value
//            .set(InheritanceTaxActPage, true).success.value
//            .set(AgentOtherThanBarristerPage, true).success.value
//
//        trustDetailsMapper.build(userAnswers).value mustBe TrustDetailsType(
//          startDate = date,
//          lawCountry = Some("FR"),
//          administrationCountry = Some("GB"),
//          residentialStatus = Some(ResidentialStatusType(
//            uk = None,
//            nonUK = Some(NonUKType(false,Some(true),Some(true),None))
//          )),
//
//          typeOfTrust = WillTrustOrIntestacyTrust,
//          deedOfVariation = None,
//          interVivos = None,
//          efrbsStartDate = None
//        )
//
//      }
//
//      "must able to create TrustDetails for a non-UK governed, UK admin, not for schedule 5A, no Inheritance tax act 1984" in {
//        val date = LocalDate.of(2010, 10, 10)
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(TrustNamePage, "New Trust").success.value
//            .set(WhenTrustSetupPage, date).success.value
//            .set(GovernedInsideTheUKPage, false).success.value
//            .set(CountryGoverningTrustPage, "FR").success.value
//            .set(AdministrationInsideUKPage, true).success.value
//            .set(TrustResidentInUKPage, false).success.value
//            .set(RegisteringTrustFor5APage, false).success.value
//            .set(InheritanceTaxActPage, false).success.value
//
//
//        trustDetailsMapper.build(userAnswers).value mustBe TrustDetailsType(
//          startDate = date,
//          lawCountry = Some("FR"),
//          administrationCountry = Some("GB"),
//          residentialStatus = Some(ResidentialStatusType(
//            uk = None,
//            nonUK = Some(NonUKType(false,Some(false),None,None))
//          )),
//
//          typeOfTrust = WillTrustOrIntestacyTrust,
//          deedOfVariation = None,
//          interVivos = None,
//          efrbsStartDate = None
//        )
//
//      }
//
//      "must not able  to create TrustDetails for a non-UK governed, UK admin, not for schedule 5A and no inheritance tax available" in {
//        val date = LocalDate.of(2010, 10, 10)
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(TrustNamePage, "New Trust").success.value
//            .set(WhenTrustSetupPage, date).success.value
//            .set(GovernedInsideTheUKPage, false).success.value
//            .set(CountryGoverningTrustPage, "FR").success.value
//            .set(AdministrationInsideUKPage, true).success.value
//            .set(TrustResidentInUKPage, false).success.value
//            .set(RegisteringTrustFor5APage, false).success.value
//
//
//        trustDetailsMapper.build(userAnswers) mustBe None
//
//      }
//
//      "must not able  to create TrustDetails when only trust name and setup details available" in {
//        val date = LocalDate.of(2010, 10, 10)
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(TrustNamePage, "New Trust").success.value
//            .set(WhenTrustSetupPage, date).success.value
//
//
//
//        trustDetailsMapper.build(userAnswers) mustBe None
//
//      }

    }

  }

}
