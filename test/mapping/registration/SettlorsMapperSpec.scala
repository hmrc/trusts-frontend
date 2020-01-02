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
import mapping._
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.PassportOrIdCardDetails
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.settlors.living_settlor._

class SettlorsMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers  {

  val settlorsMapper: Mapping[Settlors] = injector.instanceOf[SettlorsMapper]

  val dateOfBirth: LocalDate = LocalDate.of(1944, 10, 10)

  "SettlorsMapper" - {

    "when user answers is empty" - {

      "must not be able to create a Settlors model" in {

        val userAnswers = emptyUserAnswers

        settlorsMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "must be able to create a Settlors model with settlor individuals" in {

      val dateOfBirth: LocalDate = LocalDate.of(1944, 10, 10)
      val expiryDate = LocalDate.of(2020, 10, 10)

      val userAnswers =
        emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
          .set(SettlorIndividualDateOfBirthPage(0), dateOfBirth).success.value
          .set(SettlorIndividualAddressYesNoPage(0), true).success.value
          .set(SettlorIndividualNINOYesNoPage(0), false).success.value
          .set(SettlorIndividualPassportYesNoPage(0), true).success.value
          .set(SettlorIndividualPassportPage(0), PassportOrIdCardDetails("UK", "2345678", expiryDate)).success.value
          .set(SettlorIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(1), FullName("Another", None, "Name")).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(1), false).success.value
          .set(SettlorIndividualAddressYesNoPage(1), true).success.value
          .set(SettlorIndividualNINOYesNoPage(1), false).success.value
          .set(SettlorIndividualPassportYesNoPage(1), true).success.value
          .set(SettlorIndividualPassportPage(1), PassportOrIdCardDetails("UK", "1234567", expiryDate)).success.value

      settlorsMapper.build(userAnswers).value mustBe Settlors(
        settlor = Some(List(
          Settlor(
            NameType("First", None, "Last"), Some(dateOfBirth),
            Some(IdentificationType(None, Some(PassportType("2345678", expiryDate, "UK")),None))),
          Settlor(
            NameType("Another", None, "Name"),
            None,
            Some(IdentificationType(None, Some(PassportType("1234567", expiryDate, "UK")),None))))
        ),
        settlorCompany = None)
    }
  }
}
