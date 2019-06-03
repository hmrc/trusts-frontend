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
import models.{FullName, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._

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

        "must not be able to create declaration when not have all answers" in {

          val userAnswers = emptyUserAnswers
            .set(DeclarationPage, FullName("First", None, "Last")).success.value

          declarationMapper.build(userAnswers) mustNot be(defined)
        }

        "must be able to create declaration when user answers given" in {

          val userAnswers = emptyUserAnswers
            .set(DeclarationPage, FullName("First", None, "Last")).success.value
            .set(AgentAddressYesNoPage, true).success.value
            .set(AgentUKAddressPage, UKAddress("Line1", Some("line2"), None, "Newcastle", "NE62RT")).success.value

          declarationMapper.build(userAnswers).value mustBe Declaration(
            name = NameType("First", None, "Last"),
            address = AddressType("Line1", "line2", None, Some("Newcastle"), Some("NE62RT"), "GB")
          )

        }
      }
    }

  }

}
