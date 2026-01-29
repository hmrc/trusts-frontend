/*
 * Copyright 2024 HM Revenue & Customs
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
import models.core.http.MatchData
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import pages.register.{PostcodeForTheTrustPage, TrustHaveAUTRPage, WhatIsTheUTRPage}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers

class MatchingMapperSpec extends AnyFreeSpec with Matchers with OptionValues with Generators with SpecBaseHelpers {

  val matchingMapper: MatchingMapper = injector.instanceOf[MatchingMapper]

  val trustName                  = "Trust Name"
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "MatchingMapper" - {

    "when userAnswers is empty" - {

      "must not create Matching" in {
        val userAnswers = TestUserAnswers.emptyUserAnswers

        matchingMapper.build(userAnswers, trustName) mustNot be(defined)
      }

    }

    "when userAnswers is not empty" - {

      "for a new trust" - {

        "must not create Matching" in {

          val userAnswers = TestUserAnswers.emptyUserAnswers
            .set(TrustHaveAUTRPage, false)
            .success
            .value

          matchingMapper.build(userAnswers, trustName) mustNot be(defined)

        }

      }

      "for an existing trust" - {

        "must create matching for a successful matching trust" in {
          val userAnswers = TestUserAnswers.emptyUserAnswers
            .set(WhatIsTheUTRPage, "1234567890")
            .success
            .value
            .set(PostcodeForTheTrustPage, "NE981ZZ")
            .success
            .value

          val result = matchingMapper.build(userAnswers, trustName).value

          result mustBe MatchData(
            utr = "1234567890",
            name = "Trust Name",
            postCode = Some("NE981ZZ")
          )
        }

      }

    }

  }

}
