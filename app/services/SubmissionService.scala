/*
 * Copyright 2023 HM Revenue & Customs
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

package services

import com.google.inject.ImplementedBy
import connector.TrustConnector
import mapping.registration.RegistrationMapper
import models.core.UserAnswers
import models.core.http.TrustResponse._
import models.core.http.{AddressType, Registration, RegistrationTRNResponse, TrustResponse}
import models.requests.RegistrationDataRequest
import pages.register.suitability.{ExpressTrustYesNoPage, TrustTaxableYesNoPage}
import play.api.Logging
import play.api.libs.json._
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.Session

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefaultSubmissionService @Inject()(
                                          registrationMapper: RegistrationMapper,
                                          trustConnector: TrustConnector,
                                          auditService: AuditService,
                                          registrationsRepository: RegistrationsRepository
                                        ) extends SubmissionService with Logging {

  override def submit(userAnswers: UserAnswers)
                     (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {
    logger.info(s"[submit][Session ID: ${request.sessionId}] submitting registration")
    getCorrespondenceAddress(userAnswers)
  }

  private def getCorrespondenceAddress(userAnswers: UserAnswers)
                                      (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {
    registrationsRepository.getCorrespondenceAddress(userAnswers.draftId).flatMap {
      correspondenceAddress =>
        getTrustName(userAnswers, correspondenceAddress)
    }.recover {
      case e =>
        logger.error(s"[getCorrespondenceAddress][Session ID: ${Session.id(hc)}] Unable to get correspondence address: ${e.getMessage}")
        auditService.auditRegistrationPreparationFailed(userAnswers, "Error retrieving correspondence address transformation.")
        UnableToRegister()
    }
  }

  private def getTrustName(userAnswers: UserAnswers, correspondenceAddress: AddressType)
                          (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {
    registrationsRepository.getTrustName(userAnswers.draftId).flatMap {
      trustName =>
        buildRegistration(userAnswers, correspondenceAddress, trustName)
    }.recover {
      case e =>
        logger.error(s"[getTrustName][Session ID: ${Session.id(hc)}] Unable to get trust name: ${e.getMessage}")
        auditService.auditRegistrationPreparationFailed(userAnswers, "Error retrieving trust name transformation.")
        UnableToRegister()
    }
  }

  private def buildRegistration(userAnswers: UserAnswers, correspondenceAddress: AddressType, trustName: String)
                               (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {
    registrationMapper.build(userAnswers, correspondenceAddress, trustName, request.affinityGroup).flatMap {
      case Some(registration) =>
        addDraftRegistrationSections(userAnswers, registration)
      case _ =>
        logger.error(s"[buildRegistration][Session ID: ${Session.id(hc)}] Unable to generate registration to submit.")
        auditService.auditRegistrationPreparationFailed(userAnswers, "Error mapping UserAnswers to Registration.")
        Future.failed(UnableToRegister())
    }
  }

  private def addDraftRegistrationSections(userAnswers: UserAnswers, registration: Registration)
                                          (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {
    registrationsRepository.addDraftRegistrationSections(userAnswers.draftId, Json.toJson(registration)).flatMap {
      registrationJson =>
        val fullRegistrationJson = add5mldData(registrationJson, userAnswers)
        register(userAnswers.draftId, fullRegistrationJson)
    }.recover {
      case e =>
        logger.error(s"[addDraftRegistrationSections][Session ID: ${Session.id(hc)}] Unable to add draft registration sections: ${e.getMessage}")
        auditService.auditRegistrationPreparationFailed(userAnswers, "Error adding draft registration sections.")
        UnableToRegister()
    }
  }

  private def register(draftId: String, fullRegistrationJson: JsValue)
                      (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {

    trustConnector.register(fullRegistrationJson, draftId) map {
      case response@RegistrationTRNResponse(_) =>
        logger.info(s"[register][Session ID: ${Session.id(hc)}] Registration successfully submitted.")
        auditService.auditRegistrationSubmitted(fullRegistrationJson, draftId, response)
        response
      case AlreadyRegistered =>
        logger.warn(s"[register][Session ID: ${Session.id(hc)}] Registration already submitted.")
        auditService.auditRegistrationAlreadySubmitted(fullRegistrationJson, draftId)
        AlreadyRegistered
      case other =>
        logger.warn(s"[register][Session ID: ${Session.id(hc)}] Registration submission failed.")
        auditService.auditRegistrationSubmissionFailed(fullRegistrationJson, draftId)
        other
    }
  }

  private def add5mldData(registrationJson: JsValue, userAnswers: UserAnswers): JsValue = {
      def putNewValue[T](path: JsPath, value: Option[T])(implicit wts: Writes[T]): Reads[JsObject] = {
        value match {
          case Some(v) =>
            __.json.update(path.json.put(Json.toJson(v)))
          case _ =>
            logger.warn(s"[add5mldData][putNewValue] value not found at $path")
            __.json.pick[JsObject]
        }
      }

      registrationJson.transform(
        putNewValue(__ \ 'trust \ 'details \ 'expressTrust, userAnswers.get(ExpressTrustYesNoPage)) andThen
          putNewValue(__ \ 'trust \ 'details \ 'trustTaxable, userAnswers.get(TrustTaxableYesNoPage))
      ).fold(
        _ => {
          logger.error("[add5mldData] Could not add expressTrust and trustTaxable data to registration JSON")
          registrationJson
        },
        value => value
      )
  }
}

@ImplementedBy(classOf[DefaultSubmissionService])
trait SubmissionService {

  def submit(userAnswers: UserAnswers)
            (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse]

}


