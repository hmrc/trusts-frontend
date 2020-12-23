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

import _root_.pages.register.DeclarationPage
import _root_.pages.register.agents.{AgentAddressYesNoPage, AgentInternalReferencePage, AgentInternationalAddressPage, AgentUKAddressPage}
import config.FrontendAppConfig
import models.core.http.{AddressType, Declaration}
import models.core.pages.{Declaration => DeclarationFormModel}
import models.core.{UserAnswers, pages}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationMapper @Inject()(addressMapper: AddressMapper,
                                  registrationsRepository: RegistrationsRepository,
                                  config: FrontendAppConfig) {

  def build(userAnswers: UserAnswers, leadTrusteeAddress: AddressType)
           (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[Declaration]] = {

    val declaration: Option[pages.Declaration] = userAnswers.get(DeclarationPage)

    if (config.agentDetailsMicroserviceEnabled) {
      buildDeclarationFromExternalValues(userAnswers, leadTrusteeAddress, declaration)
    } else {
      buildDeclarationFromLocalValues(userAnswers, leadTrusteeAddress, declaration)
    }
  }

  private def buildDeclarationFromLocalValues(userAnswers: UserAnswers,
                                              leadTrusteeAddress: AddressType,
                                              declaration: Option[DeclarationFormModel]): Future[Option[Declaration]] = {

    val agentInternalReference: Option[String] = userAnswers.get(AgentInternalReferencePage)

    val agentAddress: Option[AddressType] = {
      addressMapper.build(
        userAnswers,
        AgentAddressYesNoPage,
        AgentUKAddressPage,
        AgentInternationalAddressPage
      )
    }

    val address: Option[AddressType] = agentInternalReference match {
      case Some(_) => agentAddress
      case _ => Some(leadTrusteeAddress)
    }

    Future.successful(address flatMap {
      declarationAddress =>
        declaration.map {
          dec =>
            Declaration(
              name = dec.name,
              address = declarationAddress
            )
        }
    })
  }

  private def buildDeclarationFromExternalValues(userAnswers: UserAnswers,
                                                 leadTrusteeAddress: AddressType,
                                                 declaration: Option[DeclarationFormModel])
                                                (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[Declaration]] = {

    for {
      agentAddress <- registrationsRepository.getAgentAddress(userAnswers)
    } yield {
      declaration map { dec =>
        Declaration(
          name = dec.name,
          address = agentAddress.getOrElse(leadTrusteeAddress)
        )
      }
    }
  }
}