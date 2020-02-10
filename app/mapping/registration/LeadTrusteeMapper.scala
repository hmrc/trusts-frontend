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
import mapping.reads.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustee, Trustees}
import mapping._
import models.core.UserAnswers


class LeadTrusteeMapper @Inject()(
                                   nameMapper: NameMapper,
                                   addressMapper: AddressMapper) extends Mapping[LeadTrusteeType] {

  override def build(userAnswers: UserAnswers): Option[LeadTrusteeType] = {

    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(List.empty[Trustee])
    trustees match {
      case Nil => None
      case list =>
        val leadTrusteeOption: Option[Trustee] = list.find(_.isLead)
        leadTrusteeOption.map { leadTrustee =>
          getLeadTrusteeType(leadTrustee)
        }
    }
  }

  private def getLeadTrusteeType(leadTrustee: Trustee): LeadTrusteeType = {
    leadTrustee match {
      case indLeadTrustee: LeadTrusteeIndividual =>
        LeadTrusteeType(
          leadTrusteeInd = Some(
            LeadTrusteeIndType(
              name = nameMapper.build(indLeadTrustee.name),
              dateOfBirth = indLeadTrustee.dateOfBirth,
              phoneNumber = indLeadTrustee.telephoneNumber,
              identification = IdentificationType(
                nino = indLeadTrustee.nino,
                passport = None,
                address = None
              )
            )
          ),
          leadTrusteeOrg = None
        )
      case orgLeadTrustee: LeadTrusteeOrganisation =>
        LeadTrusteeType(
          leadTrusteeInd = None,
          leadTrusteeOrg = Some(
            LeadTrusteeOrgType(
              name = orgLeadTrustee.name,
              phoneNumber = orgLeadTrustee.telephoneNumber,
              email = None,
              identification = IdentificationOrgType(
                utr = orgLeadTrustee.utr,
                address = None
              )
            )
          )
        )
    }

  }
}
