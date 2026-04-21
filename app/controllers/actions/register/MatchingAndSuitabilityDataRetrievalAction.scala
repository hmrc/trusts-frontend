/*
 * Copyright 2026 HM Revenue & Customs
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

import models.requests.{IdentifierRequest, OptionalMatchingAndSuitabilityDataRequest}
import play.api.mvc.ActionTransformer
import repositories.CacheRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MatchingAndSuitabilityDataRetrievalActionImpl @Inject() (cacheRepository: CacheRepository)(implicit
  val executionContext: ExecutionContext
) extends MatchingAndSuitabilityDataRetrievalAction {

  override protected def transform[A](
    request: IdentifierRequest[A]
  ): Future[OptionalMatchingAndSuitabilityDataRequest[A]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    cacheRepository.get(request.internalId).flatMap { ua =>
      val dataRequest = OptionalMatchingAndSuitabilityDataRequest(
        request = request.request,
        internalId = request.internalId,
        sessionId = Session.id(hc),
        userAnswers = ua,
        affinityGroup = request.affinityGroup,
        enrolments = request.enrolments,
        agentARN = request.agentARN
      )

      Future.successful(dataRequest)
    }
  }

}

trait MatchingAndSuitabilityDataRetrievalAction
    extends ActionTransformer[IdentifierRequest, OptionalMatchingAndSuitabilityDataRequest]
