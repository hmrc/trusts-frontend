/*
 * Copyright 2021 HM Revenue & Customs
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
import models.core.UserAnswers
import models.core.http.AgentDetails
import pages.register.agents._

class AgentMapper @Inject()(addressMapper : AddressMapper) extends Mapping[AgentDetails] {

  override def build(userAnswers: UserAnswers): Option[AgentDetails] = {

    for {
      arn <- userAnswers.get(AgentARNPage)
      agentName <- userAnswers.get(AgentNamePage)
      address <- addressMapper.build(
        userAnswers,
        AgentAddressYesNoPage,
        AgentUKAddressPage,
        AgentInternationalAddressPage
      )
      telephone <- userAnswers.get(AgentTelephoneNumberPage)
      internalReference <- userAnswers.get(AgentInternalReferencePage)
    } yield {
      AgentDetails(arn, agentName, address, telephone, internalReference)
    }

  }

}
