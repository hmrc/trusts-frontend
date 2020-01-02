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

import javax.inject.Inject
import mapping.Mapping
import models.core.UserAnswers

class TaxLiabilityMapper @Inject()() extends Mapping[YearsReturns] {

  /**
    * Author: Adam Conder
    * YearsReturns A.K.A Years of tax consequence is not part of the MVP.
    * Therefore at the present time there should be no YearsReturns sent to ETMP/CESA.
    * For more information on this matter or to see the discussion please refer to TRUS-1193.
    */

  override def build(userAnswers: UserAnswers): Option[YearsReturns] = {
    None
  }

}
