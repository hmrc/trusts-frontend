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

import com.google.inject.Inject
import connector.EnrolmentStoreConnector
import models.requests.DataRequest
import pages.WhatIsTheUTRVariationPage
import play.api.Logger
import play.api.mvc.Results.Redirect
import play.api.mvc._
import services.AgentAuthenticationService
import uk.gov.hmrc.auth.core.FailedRelationship
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class PlaybackAction @Inject()(val parser: BodyParsers.Default,
                               enrolmentStoreConnector: EnrolmentStoreConnector,
                               agentAuthenticationService: AgentAuthenticationService
                              )(override implicit val executionContext: ExecutionContext) extends ActionRefiner[DataRequest, DataRequest] {

  override def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    val redirectToLogin: Result = agentAuthenticationService.redirectToLogin

    request.userAnswers.get(WhatIsTheUTRVariationPage) map { utr =>

      agentAuthenticationService.authenticate(utr)(request, hc, implicitly) recoverWith {
        case FailedRelationship(msg) =>
          Logger.info(s"[IdentifyForPlayback] Relationship does not exist in Trust IV for user due to $msg")
          Future.successful(Left(redirectToLogin))
      }

    } getOrElse Future.successful(Left(Redirect(controllers.routes.IndexController.onPageLoad())))

  }

}
