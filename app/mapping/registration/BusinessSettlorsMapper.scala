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
import mapping._
import mapping.reads.BusinessSettlor
import models.core.UserAnswers

class BusinessSettlorsMapper @Inject()(addressMapper: AddressMapper) extends Mapping[List[SettlorCompany]] {

   def build(userAnswers: UserAnswers): Option[List[SettlorCompany]] = {
     val settlors = userAnswers.get(mapping.reads.BusinessSettlors).getOrElse(List.empty[BusinessSettlor])

     val mappedSettlors = settlors.map { ls =>
       SettlorCompany(ls.name, ls.companyType, ls.companyTime, identificationOrgMap(ls))
     }

     mappedSettlors match {
       case Nil => None
       case _ => Some(mappedSettlors)
     }
  }



  private def identificationOrgMap(settlor: BusinessSettlor): Option[IdentificationOrgType] = {

    val identificationType = IdentificationOrgType(
      settlor.utr,
      addressMapper.build(settlor.address)
    )

    identificationType match {
      case IdentificationOrgType(None, None) => None
      case _ => Some(identificationType)
    }
  }

}
