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
import mapping.reads.IndividualSettlor
import models.{PassportOrIdCardDetails, UserAnswers}

class IndividualSettlorsMapper @Inject()(nameMapper: NameMapper, addressMapper: AddressMapper) extends Mapping[List[Settlor]] {

   def build(userAnswers: UserAnswers): Option[List[Settlor]] = {
     val settlors = userAnswers.get(mapping.reads.LivingSettlors).getOrElse(List.empty[IndividualSettlor])

     val mappedSettlors = settlors.map { ls =>
       Settlor(nameMapper.build(ls.name), ls.dateOfBirth, identificationMap(ls))
     }

     mappedSettlors match {
       case Nil => None
       case _ => Some(mappedSettlors)
     }
  }

  private def identificationMap(settlor: IndividualSettlor): Option[IdentificationType] = {

    val identificationType = IdentificationType(
      settlor.nino,
      passportMap(settlor.passport),
      addressMapper.build(settlor.address)
    )

    identificationType match {
      case IdentificationType(None, None, None) => None
      case _ => Some(identificationType)
    }
  }

  private def passportMap(passportOrIdCardDetails: Option[PassportOrIdCardDetails]): Option[PassportType] = {
    passportOrIdCardDetails map { passportOrIdCardDetails =>
      PassportType(passportOrIdCardDetails.cardNumber, passportOrIdCardDetails.expiryDate, passportOrIdCardDetails.country)
    }
  }
}
