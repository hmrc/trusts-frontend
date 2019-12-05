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

import javax.inject.Inject
import mapping.Mapping
import models.core.UserAnswers
import play.api.Logger

class BeneficiariesMapper @Inject()(
                                     individualMapper: IndividualBeneficiaryMapper,
                                     unidentifiedMapper: ClassOfBeneficiariesMapper
                                   ) extends Mapping[BeneficiaryType] {
  override def build(userAnswers: UserAnswers): Option[BeneficiaryType] = {
    val individuals = individualMapper.build(userAnswers)
    val unidentified = unidentifiedMapper.build(userAnswers)

    if (individuals.isDefined || unidentified.isDefined) {
      Some(
        BeneficiaryType(
          individualDetails = individuals,
          company = None,
          trust = None,
          charity = None,
          unidentified = unidentified,
          large = None,
          other = None
        )
      )
    } else {
      Logger.info(s"[BeneficiariesMapper][build] unable to map beneficiaries")
      None
    }
  }
}
