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

import com.google.inject.ImplementedBy
import config.FrontendAppConfig
import connector.EnrolmentStoreConnector
import controllers.playback.routes
import handlers.ErrorHandler
import javax.inject.Inject
import models.EnrolmentStoreResponse.{AlreadyClaimed, NotClaimed}
import models.requests.DataRequest
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AuthenticationServiceImpl @Inject()(
                                           enrolmentStoreConnector: EnrolmentStoreConnector,
                                           config: FrontendAppConfig,
                                           errorHandler: ErrorHandler,
                                           trustsIV: TrustsIV,
                                           implicit val ec: ExecutionContext
                                         ) extends AuthenticationService {

  def authenticate[A](utr: String)(implicit request: DataRequest[A], hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]] =
    request.affinityGroup match {
      case Agent => checkIfAgentAuthorised(utr)
      case _ => checkIfTrustIsClaimedAndTrustIV(utr)
    }

  private def checkIfTrustIsClaimedAndTrustIV[A](utr: String)(implicit request: DataRequest[A], hc : HeaderCarrier): Future[Either[Result, DataRequest[A]]] = {

    val userEnrolled = checkForTrustEnrolmentForUTR(utr)

    if (userEnrolled) {
      // TODO TRUS-2015 send to Trust IV for a non claiming journey when no relationship in IV exists
      trustsIV.authenticate(
        utr = utr,
        onIVRelationshipExisting = Future.successful(Right(request)),
        onIVRelationshipNotExisting = Future.successful(Left(Redirect(config.claimATrustUrl(utr))))
      )
    } else {
      enrolmentStoreConnector.checkIfAlreadyClaimed(utr) flatMap {
        case AlreadyClaimed =>
          Future.successful(Left(Redirect(routes.TrustStatusController.alreadyClaimed())))
        case NotClaimed =>
          Future.successful(Left(Redirect(config.claimATrustUrl(utr))))
        case _ =>
          Future.successful(Left(InternalServerError(errorHandler.internalServerErrorTemplate)))
      }
    }
  }

  private def checkIfAgentAuthorised[A](utr: String)(implicit request: DataRequest[A], hc : HeaderCarrier): Future[Either[Result, DataRequest[A]]] =
    enrolmentStoreConnector.checkIfAlreadyClaimed(utr) map {
      case NotClaimed =>
        Left(Redirect(routes.TrustNotClaimedController.onPageLoad()))
      case AlreadyClaimed =>

        val agentEnrolled = checkForTrustEnrolmentForUTR(utr)

        if (agentEnrolled) {
          Right(request)
        } else {
          Left(Redirect(routes.AgentNotAuthorisedController.onPageLoad()))
        }
      case _ =>
        Left(InternalServerError(errorHandler.internalServerErrorTemplate))
    }

  private def checkForTrustEnrolmentForUTR[A](utr: String)(implicit request: DataRequest[A]): Boolean =
    request.enrolments.enrolments
      .find(_.key equals config.serviceName)
      .flatMap(_.identifiers.find(_.key equals "SAUTR"))
      .exists(_.value equals utr)

}

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {
  def authenticate[A](utr: String)(implicit request: DataRequest[A], hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]]
}
