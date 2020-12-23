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

import base.SpecBaseHelpers
import generators.Generators
import models.core.http.{AddressType, Declaration}
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.DeclarationPage
import pages.register.agents.{AgentAddressYesNoPage, AgentInternalReferencePage, AgentInternationalAddressPage, AgentUKAddressPage}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DeclarationMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private val declarationMapper: DeclarationMapper = injector.instanceOf[DeclarationMapper]

  private val addressMapper: AddressMapper = injector.instanceOf[AddressMapper]
  private val leadTrusteeUkAddress = addressMapper.build(UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ"))
  private val leadTrusteeInternationalAddress = addressMapper.build(InternationalAddress("First line", "Second line", None, "DE"))

  "DeclarationMapper" - {

    "when user answers is empty" - {

      "must not be able to create Declaration" in {
        val userAnswers = emptyUserAnswers

        Await.result(declarationMapper.build(userAnswers, leadTrusteeUkAddress), Duration.Inf) mustNot be(defined)
      }

    }

    "when user answers is not empty" - {

      "for an Agent" - {

        "must not be able to create declaration when agent UK address is not answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value

          Await.result(declarationMapper.build(userAnswers, leadTrusteeUkAddress), Duration.Inf) mustNot be(defined)
        }

        "must not be able to create declaration when declaration name is not answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(AgentAddressYesNoPage, true).success.value
            .set(AgentUKAddressPage, UKAddress("Line1", "line2", None, None, "NE62RT")).success.value

          Await.result(declarationMapper.build(userAnswers, leadTrusteeUkAddress), Duration.Inf) mustNot be(defined)
        }

        "must not be able to create declaration when agent UK address and declaration name not answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(AgentAddressYesNoPage, false).success.value

          Await.result(declarationMapper.build(userAnswers, leadTrusteeUkAddress), Duration.Inf) mustNot be(defined)
        }

        "must be able to create declaration when agent has UK address and declaration name answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
            .set(AgentAddressYesNoPage, true).success.value
            .set(AgentUKAddressPage, UKAddress("Line1", "line2", None, Some("Newcastle"), "NE62RT")).success.value

          Await.result(declarationMapper.build(userAnswers, leadTrusteeUkAddress), Duration.Inf).value mustBe Declaration(
            name = FullName("First", None, "Last"),
            address = AddressType("Line1", "line2", None, Some("Newcastle"), Some("NE62RT"), "GB")
          )

        }

        "must be able to create declaration when agent has international address and declaration name answered" in {

          val userAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, "123456789").success.value
            .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value
            .set(AgentAddressYesNoPage, false).success.value
            .set(AgentInternationalAddressPage, InternationalAddress("Line1", "line2", None, "IN")).success.value

          Await.result(declarationMapper.build(userAnswers, leadTrusteeUkAddress), Duration.Inf).value mustBe Declaration(
            name = FullName("First", None, "Last"),
            address = AddressType("Line1", "line2", None, None, None, "IN")
          )

        }
      }

      "for an Organisation" - {

        "for a lead trustee individual" - {

          "must be able to create declaration when lead trustee UK address and declaration name answered" in {

            val userAnswers = emptyUserAnswers
              .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value

            Await.result(declarationMapper.build(userAnswers, leadTrusteeUkAddress), Duration.Inf).value mustBe Declaration(
              name = FullName("First", None, "Last"),
              address = AddressType("First line", "Second line", None, Some("Newcastle"), Some("NE981ZZ"), "GB")
            )

          }

        }

        "for a lead trustee organisation" - {

          "must be able to create declaration when lead trustee UK address and declaration name answered" in {

            val userAnswers = emptyUserAnswers
              .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value

            Await.result(declarationMapper.build(userAnswers, leadTrusteeUkAddress), Duration.Inf).value mustBe Declaration(
              name = FullName("First", None, "Last"),
              address = AddressType("First line", "Second line", None, Some("Newcastle"), Some("NE981ZZ"), "GB")
            )

          }

          "must be able to create declaration when lead trustee Non-UK address and declaration name answered" in {

            val userAnswers = emptyUserAnswers
              .set(DeclarationPage, models.core.pages.Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value

            Await.result(declarationMapper.build(userAnswers, leadTrusteeInternationalAddress), Duration.Inf).value mustBe Declaration(
              name = FullName("First", None, "Last"),
              address = AddressType("First line", "Second line", None, None, None, "DE")
            )

          }
        }
      }
    }
  }
}
