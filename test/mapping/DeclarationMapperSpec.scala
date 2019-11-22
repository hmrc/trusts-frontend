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
import models.IndividualOrBusiness.Individual
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.{FullName, InternationalAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._
import pages.trustees.{IsThisLeadTrusteePage, TelephoneNumberPage, TrusteeAUKCitizenPage, TrusteeIndividualOrBusinessPage, TrusteeLiveInTheUKPage, TrusteesDateOfBirthPage, TrusteesNamePage, TrusteesUkAddressPage}

class DeclarationMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  private val declarationMapper: Mapping[Declaration] = injector.instanceOf[DeclarationMapper]

  "DeclarationMapper" - {

    "when user answers is empty" - {

      "must not be able to create Declaration" in {
        val userAnswers = emptyUserAnswers

        declarationMapper.build(userAnswers) mustNot be(defined)
      }

    }

    "when user answers is not empty" - {

      "for an Agent" - {

        "must not be able to create declaration when agent UK address is not answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(DeclarationPage, FullName("First", None, "Last")).success.value

          declarationMapper.build(userAnswers) mustNot be(defined)
        }

        "must not be able to create declaration when declaration name is not answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(AgentAddressYesNoPage, true).success.value
            .set(AgentUKAddressPage, UKAddress("Line1", "line2", None, None, "NE62RT")).success.value

          declarationMapper.build(userAnswers) mustNot be(defined)
        }

        "must not be able to create declaration when agent UK address and declaration name not answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(AgentAddressYesNoPage, false).success.value

          declarationMapper.build(userAnswers) mustNot be(defined)
        }

        "must be able to create declaration when agent has UK address and declaration name answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(DeclarationPage, FullName("First", None, "Last")).success.value
            .set(AgentAddressYesNoPage, true).success.value
            .set(AgentUKAddressPage, UKAddress("Line1", "line2", None, Some("Newcastle"), "NE62RT")).success.value

          declarationMapper.build(userAnswers).value mustBe Declaration(
            name = NameType("First", None, "Last"),
            address = AddressType("Line1", "line2", None, Some("Newcastle"), Some("NE62RT"), "GB")
          )

        }

        "must be able to create declaration when agent has international address and declaration name answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(DeclarationPage, FullName("First", None, "Last")).success.value
            .set(AgentAddressYesNoPage, false).success.value
            .set(AgentInternationalAddressPage, InternationalAddress("Line1", "line2", None, "IN")).success.value

          declarationMapper.build(userAnswers).value mustBe Declaration(
            name = NameType("First", None, "Last"),
            address = AddressType("Line1", "line2", None, None, None, "IN")
          )

        }
      }

      "for an Organisation" - {


        "must not be able to create declaration when lead trustee incomplete and declaration name answered" in {

          val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")
          val userAnswers = emptyUserAnswers
            .set(DeclarationPage, FullName("First", None, "Last")).success.value
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(TrusteesNamePage(0), FullName("First", None, "Last")).success.value
            .set(TrusteesDateOfBirthPage(0), LocalDate.of(2010,10,10)).success.value
            .set(TrusteeAUKCitizenPage(0), true).success.value
            .set(TrusteeLiveInTheUKPage(0), true).success.value
            .set(TrusteesUkAddressPage(0), address).success.value

          declarationMapper.build(userAnswers) mustNot be(defined)

        }

        "must be able to create declaration when lead trustee UK address and declaration name answered" in {

          val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")
          val userAnswers = emptyUserAnswers
            .set(DeclarationPage, FullName("First", None, "Last")).success.value
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(TrusteesNamePage(0), FullName("First", None, "Last")).success.value
            .set(TrusteesDateOfBirthPage(0), LocalDate.of(2010,10,10)).success.value
            .set(TrusteeAUKCitizenPage(0), true).success.value
            .set(TrusteeLiveInTheUKPage(0), true).success.value
            .set(TrusteesUkAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          declarationMapper.build(userAnswers).value mustBe Declaration(
            name = NameType("First", None, "Last"),
            address = AddressType("First line", "Second line", None, Some("Newcastle"), Some("NE981ZZ"), "GB")
          )

        }

      }

    }

  }

}
