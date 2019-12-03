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

package services

import config.FrontendAppConfig
import connector.EnrolmentStoreConnector
import controllers.actions.TrustsAuth
import controllers.playback.routes
import javax.inject.Inject
import models.EnrolmentStoreResponse.NotClaimed
import models.requests.DataRequest
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.auth.core.{BusinessKey, Relationship}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AgentAuthenticationServiceImpl @Inject()(
                                                enrolmentStoreConnector: EnrolmentStoreConnector,
                                                config: FrontendAppConfig,
                                                trustsAuth: TrustsAuth
                                              ) extends AgentAuthenticationService {

  def redirectToLogin: Result = trustsAuth.redirectToLogin

  def authenticate[A](utr: String)(implicit request: DataRequest[A], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Result, DataRequest[A]]] = {

    trustsAuth.authorised(Relationship(trustsAuth.config.relationshipName, Set(BusinessKey(trustsAuth.config.relationshipIdentifier, utr)))) {

      request.affinityGroup match {
        case Agent => enrolmentStoreConnector.checkIfClaimed(utr) map {
          case NotClaimed => Left(Redirect(controllers.playback.routes.TrustNotClaimedController.onPageLoad()))
          case _ =>

            val agentEnrolled = checkEnrolmentOfAgent(utr)

            if (agentEnrolled) {
              Right(request)
            } else {
              Left(Redirect(routes.AgentNotAuthorisedController.onPageLoad()))
            }

        }
        case _ => Future.successful(Left(Redirect(controllers.routes.IndexController.onPageLoad())))
      }

    }

  }

  private def checkEnrolmentOfAgent[A](utr: String)(implicit request: DataRequest[A]): Boolean = {
    request.enrolments.enrolments
      .find(_.key equals config.serviceName)
      .flatMap(_.identifiers.find(_.key equals "SAUTR"))
      .exists(_.value equals utr)
  }

}

trait AgentAuthenticationService {

  def redirectToLogin: Result

  def authenticate[A](utr: String)(implicit request: DataRequest[A], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Result, DataRequest[A]]]

}
