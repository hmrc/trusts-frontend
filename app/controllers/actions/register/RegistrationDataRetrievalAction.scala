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

package controllers.actions.register

import models.core.UserAnswers
import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import play.api.Logging
import play.api.mvc.ActionTransformer
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationDataRetrievalActionImpl @Inject()(registrationsRepository: RegistrationsRepository)
                                                   (implicit val executionContext: ExecutionContext)
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
          registrationsRepository.get(draftId)
            .map(createdOptionalDataRequest(request, _))
    }
  }
}

trait RegistrationDataRetrievalAction extends ActionTransformer[IdentifierRequest, OptionalRegistrationDataRequest]
