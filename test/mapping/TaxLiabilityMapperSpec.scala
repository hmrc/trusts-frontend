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

class TaxLiabilityMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val taxLiabilityMapper: Mapping[YearsReturns] = injector.instanceOf[TaxLiabilityMapper]

  "TaxLiabilityMapper" - {

    "when user answers is empty" - {

      "must be able to create years returns" in {

        val userAnswers = emptyUserAnswers

        taxLiabilityMapper.build(userAnswers).value mustBe YearsReturns(
          returns = Some(
            List(
              YearReturnType(
                taxReturnYear = "19",
                taxConsequence = true
              )
            )
          )
        )
      }
    }

  }

}
