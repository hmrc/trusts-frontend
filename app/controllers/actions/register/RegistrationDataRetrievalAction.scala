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

package controllers.actions.register

import connector.SubmissionDraftConnector
import controllers.Assets.OK
import models.core.UserAnswers
import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import play.api.Logging
import play.api.mvc.ActionTransformer
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationDataRetrievalActionImpl @Inject()(registrationsRepository: RegistrationsRepository,
                                                    submissionDraftConnector: SubmissionDraftConnector
                                                   )(implicit val executionContext: ExecutionContext)
  extends RegistrationDataRetrievalAction with Logging {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalRegistrationDataRequest[A]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    def createdOptionalDataRequest(request: IdentifierRequest[A],
                                   userAnswers: Option[UserAnswers]): OptionalRegistrationDataRequest[A] = {
      OptionalRegistrationDataRequest(
        request = request.request,
        internalId = request.internalId,
        sessionId = Session.id(hc),
        userAnswers = userAnswers,
        affinityGroup = request.affinityGroup,
        enrolments = request.enrolments,
        agentARN = request.agentARN
      )
    }

    registrationsRepository.getMostRecentDraftId().flatMap {
        case None =>
          Future.successful(createdOptionalDataRequest(request, None))
        case Some(draftId) =>
          val adjustDraftIfNotAnAgentUser: Future[HttpResponse] = if (request.affinityGroup != Agent) {
            logger.info(s"[Draft ID: $draftId] Adjusting draft data.")
            submissionDraftConnector.adjustDraft(draftId)
          } else {
            Future.successful(HttpResponse(OK, ""))
          }

          adjustDraftIfNotAnAgentUser flatMap { _ =>
            registrationsRepository.get(draftId).map(createdOptionalDataRequest(request, _))
          } recover {
            case e =>
              logger.error(s"[Draft ID: $draftId][Session ID: ${Session.id(hc)}] call to adjust draft data failed: ${e.getMessage}")
              createdOptionalDataRequest(request, None)
          }
    }
  }
}

trait RegistrationDataRetrievalAction extends ActionTransformer[IdentifierRequest, OptionalRegistrationDataRequest]
