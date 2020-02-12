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
import mapping.reads.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustee, Trustees}
import models.core.UserAnswers
import pages.register.DeclarationPage
import pages.register.agents.{AgentAddressYesNoPage, AgentInternalReferencePage, AgentInternationalAddressPage, AgentUKAddressPage}
import pages.register.trustees.individual._
import pages.register.trustees.organisation._
import play.api.Logger

class DeclarationMapper @Inject()(nameMapper: NameMapper,
                                  addressMapper: AddressMapper) extends Mapping[Declaration] {

  override def build(userAnswers: UserAnswers): Option[Declaration] = {

    val declaration = userAnswers.get(DeclarationPage)
    val agentInternalReference = userAnswers.get(AgentInternalReferencePage)

    val address = agentInternalReference match {
      case Some(_) => getAgentAddress(userAnswers)
      case _ => getLeadTrusteeAddress(userAnswers)
    }

    address flatMap {
      declarationAddress =>
        declaration.map {
          dec =>
            Declaration(
              name = nameMapper.build(dec.name),
              address = declarationAddress
            )
        }
    }
  }

  private def getLeadTrusteeAddress(userAnswers: UserAnswers): Option[AddressType] = {
    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(List.empty[Trustee])
    trustees match {
      case Nil =>
        Logger.info(s"[DeclarationMapper][build] unable to create declaration due to not having any trustees")
        None
      case list =>
        list.find(_.isLead).flatMap {
          case lti: LeadTrusteeIndividual =>
            val index = list.indexOf(lti)
            addressMapper.build(
              userAnswers,
              TrusteeAddressInTheUKPage(index),
              TrusteesUkAddressPage(index),
              TrusteesInternationalAddressPage(index)
            )
          case lto: LeadTrusteeOrganisation =>
            val index = list.indexOf(lto)
            addressMapper.build(
              userAnswers,
              TrusteeOrgAddressUkYesNoPage(index),
              TrusteeOrgAddressUkPage(index),
              TrusteeOrgAddressInternationalPage(index)
            )
          case _ =>
            Logger.info(s"[CorrespondenceMapper][build] unable to create correspondence due to trustees not having a lead")
            None
        }
    }
  }

  private def getAgentAddress(userAnswers: UserAnswers): Option[AddressType] = {
    addressMapper.build(
      userAnswers,
      AgentAddressYesNoPage,
      AgentUKAddressPage,
      AgentInternationalAddressPage
    )
  }
}