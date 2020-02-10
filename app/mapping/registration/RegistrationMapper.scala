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
import models.core.UserAnswers
import play.api.Logger

class RegistrationMapper @Inject()(
                                    declarationMapper: DeclarationMapper,
                                    correspondenceMapper: CorrespondenceMapper,
                                    trustDetailsMapper: TrustDetailsMapper,
                                    beneficiariesMapper: BeneficiariesMapper,
                                    assetMapper: AssetMapper,
                                    leadTrusteeMapper: LeadTrusteeMapper,
                                    agentMapper: AgentMapper,
                                    deceasedSettlorMapper: DeceasedSettlorMapper,
                                    taxLiabilityMapper: TaxLiabilityMapper,
                                    trusteeMapper: TrusteeMapper,
                                    settlorMapper: SettlorsMapper,
                                    matchingMapper: MatchingMapper
                                  ) extends Mapping[Registration] {

  override def build(userAnswers: UserAnswers): Option[Registration] = {

    for {
      trustDetails <- trustDetailsMapper.build(userAnswers)
      assets <- assetMapper.build(userAnswers)
      correspondence <- correspondenceMapper.build(userAnswers)
      beneficiaries <- beneficiariesMapper.build(userAnswers)
      leadTrustees <- leadTrusteeMapper.build(userAnswers)
      declaration <- declarationMapper.build(userAnswers)
    } yield {

      Logger.info("*********************************************")
      Logger.info(s"Lead trustees: $leadTrustees")
      Logger.info("*********************************************")

      val agent = agentMapper.build(userAnswers)
      val deceasedSettlor = deceasedSettlorMapper.build(userAnswers)
      val taxLiability = taxLiabilityMapper.build(userAnswers)
      val trustees = trusteeMapper.build(userAnswers)

      Logger.info("*********************************************")
      Logger.info(s"Trustees: $trustees")
      Logger.info("*********************************************")

      val settlors = settlorMapper.build(userAnswers)

      val entities = TrustEntitiesType(
        naturalPerson = None,
        beneficiary = beneficiaries,
        deceased = deceasedSettlor,
        leadTrustees = leadTrustees,
        trustees = trustees,
        protectors = None,
        settlors = settlors
      )

      Logger.info("*********************************************")
      Logger.info(s"Entities: $entities")
      Logger.info("*********************************************")

      val reg = Registration(
        matchData = matchingMapper.build(userAnswers),
        correspondence = correspondence,
        yearsReturns = taxLiability,
        declaration = declaration,
        trust = Trust(
          details = trustDetails,
          entities = entities,
          assets = assets
        ),
        agentDetails = agent
      )

      Logger.info("*********************************************")
      Logger.info(s"Registration: $reg")
      Logger.info("*********************************************")

      reg
    }

  }

}
