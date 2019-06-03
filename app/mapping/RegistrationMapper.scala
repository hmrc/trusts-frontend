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
import play.api.Logger
import play.api.libs.json.Json

class RegistrationMapper @Inject()(
                                  correspondenceMapper: CorrespondenceMapper,
                                  trustDetailsMapper: TrustDetailsMapper,
                                  beneficiariesMapper: BeneficiariesMapper,
                                  assetMapper: AssetMapper,
                                  leadTrusteeMapper: LeadTrusteeMapper,
                                  agentMapper: AgentMapper,
                                  deceasedSettlorMapper: DeceasedSettlorMapper,
                                  taxLiabilityMapper: TaxLiabilityMapper,
                                  trusteeMapper: TrusteeMapper
                                  ) extends Mapping[Registration] {

  override def build(userAnswers: UserAnswers): Option[Registration] = {

    for {
      trustDetails <- trustDetailsMapper.build(userAnswers)
      assets <- assetMapper.build(userAnswers)
      correspondence <- correspondenceMapper.build(userAnswers)
      beneficiaries <- beneficiariesMapper.build(userAnswers)
      leadTrustees <- leadTrusteeMapper.build(userAnswers)
    } yield {

      val agent = agentMapper.build(userAnswers)
      val deceasedSettlor = deceasedSettlorMapper.build(userAnswers)
      val taxLiability = taxLiabilityMapper.build(userAnswers)
      val trustee = trusteeMapper.build(userAnswers)

      val entities = TrustEntitiesType(
        naturalPerson = None,
        beneficiary = beneficiaries,
        deceased = deceasedSettlor,
        leadTrustees = leadTrustees,
        trustees = trustee,
        protectors = None,
        settlors = None
      )

      val registration = Registration(
        matchData = None,
        correspondence = correspondence,
        yearsReturns = taxLiability,
        declaration = null,
        trust = Trust(
          details = trustDetails,
          entities = entities,
          assets = assets
        ),
        agentDetails = agent
      )

      Logger.info(s"[RegistrationMapper][build] created the following ${Json.toJson(registration)}")

      registration
    }

  }

}
