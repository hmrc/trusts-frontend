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
import mapping.Mapping
import models.core.http.{AddressType, AgentDetails}
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.agents._

class AgentMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val agentMapper: Mapping[AgentDetails] = injector.instanceOf[AgentMapper]

  "AgentMapper" - {

    "when user answers is empty" - {

      "must not be able to create AgentDetails" in {

        val userAnswers = emptyUserAnswers

        agentMapper.build(userAnswers) mustNot be(defined)
      }
    }
    "when user answers is not empty " - {

      "must able to create AgentDetails for a UK address" in {

        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentUKAddressPage, UKAddress("Line1", "Line2", None, Some("Newcastle"), "ab1 1ab")).success.value
            .set(AgentTelephoneNumberPage, "+1234567890").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentAddressYesNoPage, true).success.value

        agentMapper.build(userAnswers).value mustBe AgentDetails(
          arn = "SARN123456",
          agentName = "Agency Name",
          agentAddress = AddressType("Line1", "Line2", None, Some("Newcastle"), Some("ab1 1ab"), "GB"),
          agentTelephoneNumber = "+1234567890",
          clientReference = "1234-5678"
        )
      }

      "must able to create AgentDetails for a UK address with only required fields" in {

        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentUKAddressPage, UKAddress("Line1", "Newcastle", None, None, "NE62RT")).success.value
            .set(AgentTelephoneNumberPage, "+1234567890").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentAddressYesNoPage, true).success.value

        agentMapper.build(userAnswers).value mustBe AgentDetails(
          arn = "SARN123456",
          agentName = "Agency Name",
          agentAddress = AddressType("Line1", "Newcastle", None, None, Some("NE62RT"), "GB"),
          agentTelephoneNumber = "+1234567890",
          clientReference = "1234-5678"
        )
      }

      "must able to create AgentDetails for a international address" in {

        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentInternationalAddressPage,InternationalAddress("line1","line2",Some("line3"), "IN")).success.value
            .set(AgentTelephoneNumberPage, "+1234567890").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentAddressYesNoPage, false).success.value

        agentMapper.build(userAnswers).value mustBe AgentDetails(
          arn = "SARN123456",
          agentName = "Agency Name",
          agentAddress = AddressType("line1", "line2", Some("line3"), None, None, "IN"),
          agentTelephoneNumber = "+1234567890",
          clientReference = "1234-5678"
        )
      }

      "must able to create AgentDetails for a international address with minimum data" in {

        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentInternationalAddressPage,InternationalAddress("line1","line2",None, "IN")).success.value
            .set(AgentTelephoneNumberPage, "+1234567890").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentAddressYesNoPage, false).success.value

        agentMapper.build(userAnswers).value mustBe AgentDetails(
          arn = "SARN123456",
          agentName = "Agency Name",
          agentAddress = AddressType("line1", "line2", None, None, None, "IN"),
          agentTelephoneNumber = "+1234567890",
          clientReference = "1234-5678"
        )
      }

      "must not be able to create AgentDetails when no address available." in {
        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentAddressYesNoPage, true).success.value
        agentMapper.build(userAnswers) mustNot be(defined)

      }

      "must not be able to create AgentDetails when no only agent name available." in {
        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
        agentMapper.build(userAnswers) mustNot be(defined)

      }
    }


  }

}
