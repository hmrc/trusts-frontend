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
import mapping.Mapping
import models.core.pages.IndividualOrBusiness._
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.DeclarationPage
import pages.register.agents.{AgentAddressYesNoPage, AgentInternalReferencePage, AgentInternationalAddressPage, AgentUKAddressPage}
import pages.register.trustees._
import pages.register.trustees.individual._
import pages.register.trustees.organisation._

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
            .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value

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
            .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
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
            .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
            .set(AgentAddressYesNoPage, false).success.value
            .set(AgentInternationalAddressPage, InternationalAddress("Line1", "line2", None, "IN")).success.value

          declarationMapper.build(userAnswers).value mustBe Declaration(
            name = NameType("First", None, "Last"),
            address = AddressType("Line1", "line2", None, None, None, "IN")
          )

        }
      }

      "for an Organisation" - {

        "for a lead trustee individual" - {

          "must not be able to create declaration when lead trustee incomplete and declaration name answered" in {

            val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

            val userAnswers = emptyUserAnswers
              .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(TrusteesNamePage(0), FullName("First", None, "Last")).success.value
              .set(TrusteesDateOfBirthPage(0), LocalDate.of(2010,10,10)).success.value
              .set(TrusteeAUKCitizenPage(0), true).success.value
              .set(TrusteeAddressInTheUKPage(0), true).success.value
              .set(TrusteesUkAddressPage(0), address).success.value

            declarationMapper.build(userAnswers) mustNot be(defined)

          }

          "must be able to create declaration when lead trustee UK address and declaration name answered" in {

            val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

            val userAnswers = emptyUserAnswers
              .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(TrusteesNamePage(0), FullName("First", None, "Last")).success.value
              .set(TrusteesDateOfBirthPage(0), LocalDate.of(2010,10,10)).success.value
              .set(TrusteeAUKCitizenPage(0), true).success.value
              .set(TrusteeAddressInTheUKPage(0), true).success.value
              .set(TrusteesUkAddressPage(0), address).success.value
              .set(TelephoneNumberPage(0), "0191 222222").success.value

            declarationMapper.build(userAnswers).value mustBe Declaration(
              name = NameType("First", None, "Last"),
              address = AddressType("First line", "Second line", None, Some("Newcastle"), Some("NE981ZZ"), "GB")
            )

          }

        }

        "for a lead trustee organisation" - {

          "must not be able to create declaration when lead trustee incomplete and declaration name answered" in {

            val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

            val userAnswers = emptyUserAnswers
              .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(TrusteeOrgNamePage(0), "Org Name").success.value
              .set(TrusteeUtrYesNoPage(0), true).success.value
              .set(TrusteesUtrPage(0), "1234567890").success.value
              .set(TrusteeOrgAddressUkYesNoPage(0), true).success.value
              .set(TrusteeOrgAddressUkPage(0), address).success.value

            declarationMapper.build(userAnswers) mustNot be(defined)

          }

          "must be able to create declaration when lead trustee UK address and declaration name answered" in {

            val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

            val userAnswers = emptyUserAnswers
              .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(TrusteeOrgNamePage(0), "Org Name").success.value
              .set(TrusteeUtrYesNoPage(0), true).success.value
              .set(TrusteesUtrPage(0), "1234567890").success.value
              .set(TrusteeOrgAddressUkYesNoPage(0), true).success.value
              .set(TrusteeOrgAddressUkPage(0), address).success.value
              .set(TelephoneNumberPage(0), "0191 222222").success.value

            declarationMapper.build(userAnswers).value mustBe Declaration(
              name = NameType("First", None, "Last"),
              address = AddressType("First line", "Second line", None, Some("Newcastle"), Some("NE981ZZ"), "GB")
            )

          }

          "must be able to create declaration when lead trustee Non-UK address and declaration name answered" in {

            val address = InternationalAddress("First line", "Second line", None, "DE")

            val userAnswers = emptyUserAnswers
              .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(TrusteeOrgNamePage(0), "Org Name").success.value
              .set(TrusteeUtrYesNoPage(0), true).success.value
              .set(TrusteesUtrPage(0), "1234567890").success.value
              .set(TrusteeOrgAddressUkYesNoPage(0), false).success.value
              .set(TrusteeOrgAddressInternationalPage(0), address).success.value
              .set(TelephoneNumberPage(0), "0191 222222").success.value

            declarationMapper.build(userAnswers).value mustBe Declaration(
              name = NameType("First", None, "Last"),
              address = AddressType("First line", "Second line", None, None, None, "DE")
            )

          }

        }

      }

    }

  }

}
