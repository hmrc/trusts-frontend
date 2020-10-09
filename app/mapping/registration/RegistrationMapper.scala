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
import uk.gov.hmrc.http.HeaderCarrier

class RegistrationMapper @Inject()(
                                    declarationMapper: DeclarationMapper,
                                    correspondenceMapper: CorrespondenceMapper,
                                    trustDetailsMapper: TrustDetailsMapper,
                                    assetMapper: AssetMapper,
                                    agentMapper: AgentMapper,
                                    matchingMapper: MatchingMapper
                                  ) {

  def build(userAnswers: UserAnswers, correspondenceAddress: AddressType, trustName: String)(implicit hc:HeaderCarrier): Option[Registration] = {

    for {
      trustDetails <- trustDetailsMapper.build(userAnswers)
      assets <- assetMapper.build(userAnswers)
      correspondence <- correspondenceMapper.build(trustName)
      declaration <- declarationMapper.build(userAnswers, correspondenceAddress)
    } yield {

      val entities = TrustEntitiesType(
        deceased = ???, ///deceasedSettlorMapper.build(userAnswers),
        settlors = ??? //settlorMapper.build(userAnswers)
      )

      Registration(
        matchData = matchingMapper.build(userAnswers, trustName),
        declaration = declaration,
        correspondence = correspondence,
        trust = Trust(
          details = trustDetails,
          entities = entities,
          assets = assets
        ),
        agentDetails = agentMapper.build(userAnswers)
      )
    }

  }
}
