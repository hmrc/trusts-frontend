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

import javax.inject.Inject
import models.UserAnswers

class TaxLiabilityMapper @Inject()() extends Mapping[YearsReturns] {

  /**
    * Author: Adam Conder
    * For MVP we are going to be sending down 1 tax year only which is the current TY '2019'.
    * Therefore, we are only sending 1 object with a true for taxConsequence.
    * This is because we agreed for MVP that we will have Trusts who have a new tax consequence.
    * This will need changed and corrected before October 2019.
    */

  override def build(userAnswers: UserAnswers): Option[YearsReturns] = {
    Some(
      YearsReturns(
        returns = Some(
          List(
            YearReturnType(
              taxReturnYear = "19",
              taxConsequence = true
            )
          )
        )
      )
    )
  }

}
