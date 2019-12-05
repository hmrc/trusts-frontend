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

package mapping.registration

import base.SpecBaseHelpers
import generators.Generators
import mapping.{Mapping, MatchData}
import models.registration.Matched
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._
import pages.register.{ExistingTrustMatched, PostcodeForTheTrustPage, TrustHaveAUTRPage, TrustNamePage, WhatIsTheUTRPage}
import utils.TestUserAnswers

class MatchingMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val matchingMapper: Mapping[MatchData] = injector.instanceOf[MatchingMapper]

  "MatchingMapper" - {

    "when userAnswers is empty" - {

      "must not create Matching" in {
        val userAnswers = TestUserAnswers.emptyUserAnswers

        matchingMapper.build(userAnswers) mustNot be(defined)
      }

    }

    "when userAnswers is not empty" - {

      "for a new trust" - {

        "must not create Matching" in {

          val userAnswers = TestUserAnswers.emptyUserAnswers
            .set(TrustHaveAUTRPage, false).success.value

          matchingMapper.build(userAnswers) mustNot be(defined)

        }

      }

      "for an existing trust" - {

        "must not create matching for an AlreadyRegistered trust" in {
          val userAnswers = TestUserAnswers.emptyUserAnswers
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "1234567890").success.value
            .set(TrustNamePage, "Existing trust").success.value
            .set(PostcodeForTheTrustPage, "NE981ZZ").success.value
            .set(ExistingTrustMatched, Matched.AlreadyRegistered).success.value

          matchingMapper.build(userAnswers) mustNot be(defined)
        }

        "must not create matching for a Failed matching trust" in {
          val userAnswers = TestUserAnswers.emptyUserAnswers
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "1234567890").success.value
            .set(TrustNamePage, "Existing trust").success.value
            .set(PostcodeForTheTrustPage, "NE981ZZ").success.value
            .set(ExistingTrustMatched, Matched.Failed).success.value

          matchingMapper.build(userAnswers) mustNot be(defined)
        }

        "must create matching for a successful matching trust" in {
          val userAnswers = TestUserAnswers.emptyUserAnswers
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "1234567890").success.value
            .set(TrustNamePage, "Existing trust").success.value
            .set(PostcodeForTheTrustPage, "NE981ZZ").success.value
            .set(ExistingTrustMatched, Matched.Success).success.value

          val result = matchingMapper.build(userAnswers).value

          result mustBe MatchData(
            utr = "1234567890",
            name = "Existing trust",
            postCode = Some("NE981ZZ")
          )
        }

      }

    }

  }

}
