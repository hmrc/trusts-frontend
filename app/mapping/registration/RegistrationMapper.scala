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
import models.core.UserAnswers

class RegistrationMapper @Inject()(
                                    declarationMapper: DeclarationMapper,
                                    correspondenceMapper: CorrespondenceMapper,
                                    trustDetailsMapper: TrustDetailsMapper,
                                    assetMapper: AssetMapper,
                                    agentMapper: AgentMapper,
                                    deceasedSettlorMapper: DeceasedSettlorMapper,
                                    taxLiabilityMapper: TaxLiabilityMapper,
                                    settlorMapper: SettlorsMapper,
                                    matchingMapper: MatchingMapper
                                  ) {

  def build(userAnswers: UserAnswers, correspondenceAddress: AddressType): Option[Registration] = {

    for {
      trustDetails <- trustDetailsMapper.build(userAnswers)
      assets <- assetMapper.build(userAnswers)
      correspondence <- correspondenceMapper.build(userAnswers)
      declaration <- declarationMapper.build(userAnswers, correspondenceAddress)
    } yield {

      val agent = agentMapper.build(userAnswers)
      val deceasedSettlor = deceasedSettlorMapper.build(userAnswers)
      val taxLiability = taxLiabilityMapper.build(userAnswers)
      val trustees = None
      val settlors = settlorMapper.build(userAnswers)

      val entities = TrustEntitiesType(
        naturalPerson = None,
        beneficiary = BeneficiaryType(None, None, None, None, None, None, None),
        deceased = deceasedSettlor,
        leadTrustees = LeadTrusteeType(None, Some(LeadTrusteeOrgType("", "", None, IdentificationOrgType(None, None)))),
        trustees = trustees,
        protectors = None,
        settlors = settlors
      )

      Registration(
        matchData = matchingMapper.build(userAnswers),
        yearsReturns = taxLiability,
        declaration = declaration,
        correspondence = correspondence,
        trust = Trust(
          details = trustDetails,
          entities = entities,
          assets = assets
        ),
        agentDetails = agent
      )
    }

  }

  private def getLeadTrusteeAddress(leadTrustee: LeadTrusteeType): Option[AddressType] = {
    leadTrustee match {
      case LeadTrusteeType(Some(trusteeInd), None) => trusteeInd.identification.address
      case LeadTrusteeType(None, Some(trusteeOrg)) => trusteeOrg.identification.address
      case _ => None
    }
  }

  private def getLeadTrusteePhoneNumber(leadTrustee: LeadTrusteeType): Option[String] = {
    leadTrustee match {
      case LeadTrusteeType(Some(trusteeInd), None) => Some(trusteeInd.phoneNumber)
      case LeadTrusteeType(None, Some(trusteeOrg)) => Some(trusteeOrg.phoneNumber)
      case _ => None
    }
  }
}
