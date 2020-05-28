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
import mapping.reads.{Trustee, TrusteeIndividual, TrusteeOrganisation, Trustees}
import models.core.UserAnswers

class TrusteeMapper @Inject()(nameMapper: NameMapper, addressMapper: AddressMapper) extends Mapping[List[TrusteeType]] {

  override def build(userAnswers: UserAnswers): Option[List[TrusteeType]] = {
    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(List.empty[Trustee])
    val trusteesList: List[Trustee] = trustees.filter(!_.isLead)
    trusteesList match {
      case Nil => None
      case list =>
        Some(list.map { trustee =>
          getTrusteeType(trustee)
        })
    }
  }

  private def getTrusteeType(trustee: Trustee): TrusteeType = {
    trustee match {
      case indTrustee: TrusteeIndividual =>
        TrusteeType(
          trusteeInd = Some(
            TrusteeIndividualType(
              name = nameMapper.build(indTrustee.name),
              dateOfBirth = indTrustee.dateOfBirth,
              phoneNumber = None,
              identification = Some(
                IdentificationType(
                  nino = indTrustee.nino,
                  passport = None,
                  address = addressMapper.build(indTrustee.address)
                )
              )
            )
          ),
          trusteeOrg = None
        )
      case orgTrustee: TrusteeOrganisation =>
        TrusteeType(
          trusteeInd = None,
          trusteeOrg = Some(
            TrusteeOrgType(
              name = orgTrustee.name,
              phoneNumber = None,
              email = None,
              identification = IdentificationOrgType(
                utr = orgTrustee.utr,
                address = addressMapper.build(orgTrustee.address)
              )
            )
          )
        )
    }
  }
}
