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

package controllers.actions

import controllers.actions.register.MatchingAndSuitabilityDataRetrievalAction
import models.core.{MatchingAndSuitabilityUserAnswers, TrustsFrontendUserAnswers}
import models.requests.{IdentifierRequest, OptionalMatchingAndSuitabilityDataRequest}

import scala.concurrent.{ExecutionContext, Future}

class FakeMatchingAndSuitabilityDataRetrievalAction(dataToReturn: Option[TrustsFrontendUserAnswers[_]]) extends MatchingAndSuitabilityDataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalMatchingAndSuitabilityDataRequest[A]] = {
    dataToReturn match {
      case Some(x: MatchingAndSuitabilityUserAnswers) =>
        Future(OptionalMatchingAndSuitabilityDataRequest(request.request, request.internalId, "Fake Session ID", Some(x), request.affinityGroup, request.enrolments))
      case _ =>
        Future(OptionalMatchingAndSuitabilityDataRequest(request.request, request.internalId, "Fake Session ID", None, request.affinityGroup, request.enrolments))
    }
  }

  override protected implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
}
