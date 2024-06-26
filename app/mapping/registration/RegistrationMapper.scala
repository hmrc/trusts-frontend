/*
 * Copyright 2024 HM Revenue & Customs
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

import models.core.UserAnswers
import models.core.http.{AddressType, Registration}
import play.api.libs.json.Json
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationMapper @Inject()(declarationMapper: DeclarationMapper,
                                   correspondenceMapper: CorrespondenceMapper,
                                   matchingMapper: MatchingMapper) {

  def build(userAnswers: UserAnswers, correspondenceAddress: AddressType, trustName: String, affinityGroup: AffinityGroup)
           (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[Registration]] = {

    declarationMapper.build(userAnswers, correspondenceAddress).map(_.map(
      declaration =>
        Registration(
          matchData = matchingMapper.build(userAnswers, trustName),
          declaration = declaration,
          correspondence = correspondenceMapper.build(trustName),
          agentDetails = if (affinityGroup == Agent) Some(Json.obj()) else None
        )
    ))
  }
}
