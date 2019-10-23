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
import models.{FullName, InternationalAddress, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._
import pages.deceased_settlor.{SettlorDateOfBirthYesNoPage, SettlorDateOfDeathPage, SettlorDateOfDeathYesNoPage, SettlorNationalInsuranceNumberPage, SettlorsDateOfBirthPage, SettlorsInternationalAddressPage, SettlorsLastKnownAddressYesNoPage, SettlorsNINoYesNoPage, SettlorsNamePage, SettlorsUKAddressPage, WasSettlorsAddressUKYesNoPage}

class DeceasedSettlorMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val deceasedSettlorMapper: Mapping[WillType] = injector.instanceOf[DeceasedSettlorMapper]

  val dateOfBirth = LocalDate.of(1944, 10, 10)
  val dateOfDeath = LocalDate.of(1994, 10, 10)

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

      "must be able to create a deceased settlor with UK address" in {

        val userAnswers =
          emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
            .set(SettlorDateOfDeathYesNoPage, false).success.value
            .set(SettlorDateOfBirthYesNoPage, false).success.value
            .set(SettlorsNINoYesNoPage, false).success.value
            .set(SettlorsLastKnownAddressYesNoPage, true).success.value
            .set(WasSettlorsAddressUKYesNoPage, true).success.value
            .set(SettlorsUKAddressPage, UKAddress("line1", "line2", Some("line3"), Some("line4"), "ab1 1ab")).success.value

        deceasedSettlorMapper.build(userAnswers).value mustBe WillType(
          name = NameType("First", None, "Last"),
          dateOfBirth = None,
          dateOfDeath = None,
          identification = Some(Identification(None, Some(AddressType("line1", "line2", Some("line3"), Some("line4"), Some("ab1 1ab"), "GB"))))
        )

      }

      "must be able to create a deceased settlor with International address" in {

        val userAnswers =
          emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
            .set(SettlorDateOfDeathYesNoPage, false).success.value
            .set(SettlorDateOfBirthYesNoPage, false).success.value
            .set(SettlorsNINoYesNoPage, false).success.value
            .set(SettlorsLastKnownAddressYesNoPage, true).success.value
            .set(WasSettlorsAddressUKYesNoPage, false).success.value
            .set(SettlorsInternationalAddressPage, InternationalAddress("line1", "line2", Some("line3"), Some("line4"), "FR")).success.value

        deceasedSettlorMapper.build(userAnswers).value mustBe WillType(
          name = NameType("First", None, "Last"),
          dateOfBirth = None,
          dateOfDeath = None,
          identification = Some(Identification(None, Some(AddressType("line1", "line2", Some("line3"), Some("line4"), None, "FR"))))
        )

      }

      "must be able to create a deceased settlor with name, DOB, DOD and NINO" in {

        val userAnswers =
          emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
            .set(SettlorDateOfDeathYesNoPage, true).success.value
            .set(SettlorDateOfDeathPage, dateOfDeath).success.value
            .set(SettlorDateOfBirthYesNoPage, true).success.value
            .set(SettlorsDateOfBirthPage, dateOfBirth).success.value
            .set(SettlorsNINoYesNoPage, true).success.value
            .set(SettlorNationalInsuranceNumberPage, "NH111111A").success.value

        deceasedSettlorMapper.build(userAnswers).value mustBe WillType(
          name = NameType("First", None, "Last"),
          dateOfBirth = Some(dateOfBirth),
          dateOfDeath = Some(dateOfDeath),
          identification = Some(Identification(Some("NH111111A"), None))
        )

      }

      "must not able to create a deceased settlor when only 'setup after settlor died' is available" in {

        val userAnswers =
          emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value

        deceasedSettlorMapper.build(userAnswers) mustBe None

      }

    }

  }

}
