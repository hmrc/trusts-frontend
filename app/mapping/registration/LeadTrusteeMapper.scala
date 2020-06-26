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
import mapping.reads.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustee, Trustees}
import models.core.UserAnswers


class LeadTrusteeMapper @Inject()(nameMapper: NameMapper,
                                  addressMapper: AddressMapper
                                 ) extends Mapping[LeadTrusteeType] {

  override def build(userAnswers: UserAnswers): Option[LeadTrusteeType] = {

    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(List.empty[Trustee])
    trustees match {
      case Nil => None
      case list =>
        list.find(_.isLead)
            .map(buildLeadTrusteeType)
    }
  }

  private def buildLeadTrusteeType(leadTrustee: Trustee): LeadTrusteeType = {
    leadTrustee match {
      case indLeadTrustee: LeadTrusteeIndividual => buildLeadTrusteeIndividual(indLeadTrustee)
      case orgLeadTrustee: LeadTrusteeOrganisation => buildLeadTrusteeBusiness(orgLeadTrustee)
    }
  }

  private def buildLeadTrusteeIndividual(leadTrustee: LeadTrusteeIndividual) = {
    LeadTrusteeType(
      leadTrusteeInd = Some(
        LeadTrusteeIndType(
          name = nameMapper.build(leadTrustee.name),
          dateOfBirth = leadTrustee.dateOfBirth,
          phoneNumber = leadTrustee.telephoneNumber,
          identification = IdentificationType(
            nino = leadTrustee.nino,
            passport = None,
            address = None
          )
        )
      ),
      leadTrusteeOrg = None
    )
  }

  private def buildLeadTrusteeBusiness(leadTrustee: LeadTrusteeOrganisation) = {

    val identification = if(leadTrustee.utr.isDefined) {
      IdentificationOrgType(
        utr = leadTrustee.utr,
        address = None
      )
    } else {
      IdentificationOrgType(
        utr = None,
        address = addressMapper.build(leadTrustee.address)
      )
    }

      LeadTrusteeType(
        leadTrusteeInd = None,
        leadTrusteeOrg = Some(
          LeadTrusteeOrgType(
            name = leadTrustee.name,
            phoneNumber = leadTrustee.telephoneNumber,
            email = None,
            identification = identification
          )
        )
      )
  }
}
