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
import models.entities.{Trustee, TrusteeIndividual, Trustees}


class TrusteeMapper @Inject()(nameMapper: NameMapper) extends Mapping[List[TrusteeType]] {

  override def build(userAnswers: UserAnswers): Option[List[TrusteeType]] = {
    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(List.empty[Trustee])
    val trusteeIndividualList: List[Trustee] = trustees.filter(!_.isLead)
    trusteeIndividualList match {
      case Nil => None
      case list =>
        Some(list.map {
          case indTrustee: TrusteeIndividual =>
            getTrusteeType(indTrustee)
        })

    }
  }


  private def getTrusteeType(indTrustee: TrusteeIndividual): TrusteeType = {
    TrusteeType(
      trusteeInd = Some(
        TrusteeIndividualType(
          name = nameMapper.build(indTrustee.name),
          dateOfBirth = Some(indTrustee.dateOfBirth),
          phoneNumber = None,
          identification = None
        )
      ),
      None
    )
  }
}
