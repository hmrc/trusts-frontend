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

package controllers.actions

import javax.inject.Inject
import models.RegistrationProgress
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

class DraftIdDataRetrievalActionProviderImpl @Inject()(sessionRepository: SessionRepository, executionContext: ExecutionContext)
  extends DraftIdRetrievalActionProvider {

  def apply(draftId: String, status : RegistrationProgress): DraftIdDataRetrievalAction =
    new DraftIdDataRetrievalAction(draftId, status,sessionRepository, executionContext)

}

class DraftIdDataRetrievalAction(
                                  draftId : String,
                                  status : RegistrationProgress,
                                  sessionRepository: SessionRepository,
                                  implicit protected val executionContext: ExecutionContext
                                )
  extends ActionTransformer[IdentifierRequest, OptionalDataRequest] {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = {
    sessionRepository.get(draftId, request.identifier,status).map {
      userAnswers =>
        OptionalDataRequest(request.request, request.identifier, userAnswers, request.affinityGroup, request.agentARN)
    }
  }

}

trait DraftIdRetrievalActionProvider {

  def apply(draftId : String, status : RegistrationProgress = RegistrationProgress.InProgress) : DraftIdDataRetrievalAction

}