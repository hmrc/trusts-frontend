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
import models.{UKAddress, UserAnswers}
import pages.{AgentAddressYesNoPage, AgentInternalReferencePage, AgentInternationalAddressPage, AgentNamePage, AgentTelephoneNumberPage, AgentUKAddressPage}

class AgentMapper @Inject()(addressMapper : AddressMapper) extends Mapping[AgentDetails] {



  override def build(userAnswers: UserAnswers): Option[AgentDetails] = {

    for {
      agentName <- userAnswers.get(AgentNamePage)
      isUK <- userAnswers.get(AgentAddressYesNoPage)
      address <- {
        isUK match {
          case true =>addressMapper.buildUkAddress(userAnswers.get(AgentUKAddressPage))
          case false =>addressMapper.buildInternationalAddress(userAnswers.get(AgentInternationalAddressPage))
        }


      }
      telephone <- userAnswers.get(AgentTelephoneNumberPage)
      internalReference <- userAnswers.get(AgentInternalReferencePage)
    } yield {
      AgentDetails("", agentName, address, telephone, internalReference)
    }

  }
}
