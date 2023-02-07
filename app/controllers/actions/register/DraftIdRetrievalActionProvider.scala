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

package controllers.actions.register

import javax.inject.Inject
import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import play.api.mvc.ActionTransformer
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

class DraftIdDataRetrievalActionProviderImpl @Inject()(registrationsRepository: RegistrationsRepository,
                                                       executionContext: ExecutionContext)
  extends DraftIdRetrievalActionProvider {

  def apply(draftId: String): DraftIdDataRetrievalAction =
    new DraftIdDataRetrievalAction(draftId, registrationsRepository, executionContext)

}

trait DraftIdRetrievalActionProvider {

  def apply(draftId: String): DraftIdDataRetrievalAction

}

class DraftIdDataRetrievalAction(draftId: String,
                                 registrationsRepository: RegistrationsRepository,
                                 implicit protected val executionContext: ExecutionContext)
  extends ActionTransformer[IdentifierRequest, OptionalRegistrationDataRequest] {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalRegistrationDataRequest[A]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    registrationsRepository.get(draftId).map {
      userAnswers =>
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
  }

}
