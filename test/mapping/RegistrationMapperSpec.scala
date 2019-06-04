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

import base.SpecBaseHelpers
import generators.Generators
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import utils.TestUserAnswers

class RegistrationMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  private val newTrustUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val uaWithLead = TestUserAnswers.withLeadTrustee(emptyUserAnswers)
    val uaWithDeceased = TestUserAnswers.withDeceasedSettlor(uaWithLead)
    val uaWithIndBen = TestUserAnswers.withIndividualBeneficiary(uaWithDeceased)
    val uaWithTrustDetails = TestUserAnswers.withTrustDetails(uaWithIndBen)
    val asset = TestUserAnswers.withMoneyAsset(uaWithTrustDetails)
    val userAnswers = TestUserAnswers.withDeclaration(asset)

    userAnswers
  }

  val registrationMapper: Mapping[Registration] = injector.instanceOf[RegistrationMapper]

  "RegistrationMapper" - {

    "when user answers is empty" - {

      "must not be able to create Registration" in {

        val userAnswers = TestUserAnswers.emptyUserAnswers

        registrationMapper.build(userAnswers) mustNot be(defined)
      }
    }


    "when user answers is not empty" - {

      "registration is made by an Organisation" - {

        "not register an existing trust that failed matching" - {
          val userAnswers = TestUserAnswers.withMatchingFailed(newTrustUserAnswers)

          registrationMapper.build(userAnswers) mustNot be(defined)
        }

        "registering an existing trust" in {
          val userAnswers = TestUserAnswers.withMatchingSuccess(newTrustUserAnswers)

          val result = registrationMapper.build(userAnswers).value

          result.agentDetails mustNot be(defined)
          result.yearsReturns mustBe defined
          result.yearsReturns mustBe defined
          result.matchData mustNot be(defined)
          result.declaration mustBe a[Declaration]
          result.trust mustBe a[Trust]
        }

        "registering a new trust" in {

          val result = registrationMapper.build(newTrustUserAnswers).value

          result.agentDetails mustNot be(defined)
          result.yearsReturns mustBe defined
          result.yearsReturns mustBe defined
          result.matchData mustNot be(defined)
          result.declaration mustBe a[Declaration]
          result.trust mustBe a[Trust]
        }

      }

      "registration is made by an Agent" - {

        "registering a new trust" in {

          val userAnswers = TestUserAnswers.withAgent(newTrustUserAnswers)

          val result = registrationMapper.build(userAnswers).value

          result.agentDetails mustBe defined
          result.yearsReturns mustBe defined
          result.yearsReturns mustBe defined
          result.matchData mustNot be(defined)
          result.declaration mustBe a[Declaration]
          result.trust mustBe a[Trust]
        }

      }

    }

  }

}
