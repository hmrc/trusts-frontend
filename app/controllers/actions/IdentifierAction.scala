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
import config.FrontendAppConfig
import controllers.routes
import javax.inject.Singleton
import models.requests.IdentifierRequest
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Retrievals, ~}
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter
import play.api.mvc.{ActionBuilder, ActionFunction, Request, Result}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthenticatedIdentifierAction @Inject()(
                                               override val authConnector: AuthConnector,
                                               config: FrontendAppConfig,
                                               val parser: BodyParsers.Default
                                             )
                                             (implicit val executionContext: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {
  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    val hmrcAgentEnrolmentKey = "HMRC-AS-AGENT"
    authorised().retrieve(Retrievals.internalId and Retrievals.affinityGroup and Retrievals.allEnrolments) {
      case Some(internalId) ~ Some(Agent) ~ enrolments => {
        if (enrolments.getEnrolment(hmrcAgentEnrolmentKey).nonEmpty)
          block(IdentifierRequest(request, internalId, AffinityGroup.Agent))
        else
          Future(Redirect(routes.CreateAgentServicesAccountController.onPageLoad()))
      }
      case Some(internalId) ~ Some(Organisation) ~ _ => block(IdentifierRequest(request, internalId, AffinityGroup.Organisation))
      case Some(_) ~ _ ~ _ => Future(Redirect(routes.UnauthorisedController.onPageLoad()))
      case _ => throw new UnauthorizedException("Unable to retrieve internal Id")
    } recover {
      case ex: NoActiveSession => Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case ex: InsufficientEnrolments => Redirect(routes.UnauthorisedController.onPageLoad)
      case ex: InsufficientConfidenceLevel => Redirect(routes.UnauthorisedController.onPageLoad)
      case ex: UnsupportedAuthProvider => Redirect(routes.UnauthorisedController.onPageLoad)
      case ex: UnsupportedAffinityGroup => Redirect(routes.UnauthorisedController.onPageLoad)
      case ex: UnsupportedCredentialRole => Redirect(routes.UnauthorisedController.onPageLoad)
    }
  }
}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

@Singleton
class SessionIdentifierAction @Inject()(
                                         config: FrontendAppConfig,
                                         val parser: BodyParsers.Default
                                       )
                                       (implicit val executionContext: ExecutionContext) extends IdentifierAction {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    hc.sessionId match {
      case Some(session) =>
        block(IdentifierRequest(request, session.value, AffinityGroup.Individual))
      case None =>
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }
}
