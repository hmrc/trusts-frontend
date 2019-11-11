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
import models.RegistrationStatus
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import repositories.RegistrationsRepository

import scala.concurrent.{ExecutionContext, Future}

class DraftIdDataRetrievalActionProviderImpl @Inject()(registrationsRepository: RegistrationsRepository, executionContext: ExecutionContext)
  extends DraftIdRetrievalActionProvider {

  def apply(draftId: String): DraftIdDataRetrievalAction =
    new DraftIdDataRetrievalAction(draftId, registrationsRepository, executionContext)

}

class DraftIdDataRetrievalAction(
                                  draftId : String,
                                  registrationsRepository: RegistrationsRepository,
                                  implicit protected val executionContext: ExecutionContext
                                )
  extends ActionTransformer[IdentifierRequest, OptionalDataRequest] {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = {
    registrationsRepository.get(draftId, request.identifier).map {
      userAnswers =>
        OptionalDataRequest(request.request, request.identifier, userAnswers, request.affinityGroup, request.enrolments, request.agentARN)
    }
  }

}

trait DraftIdRetrievalActionProvider {

  def apply(draftId : String) : DraftIdDataRetrievalAction

}