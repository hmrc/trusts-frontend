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
import models.requests.IdentifierRequest
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, ActionFunction, Request, Result, _}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Retrievals, ~}
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierAction @Inject()(
                                               override val authConnector: AuthConnector,
                                               config: FrontendAppConfig,
                                               val parser: BodyParsers.Default
                                             )
                                             (implicit val executionContext: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {

  private def authoriseAgent[A](request : Request[A],
                                enrolments : Enrolments,
                                internalId : String,
                                block: IdentifierRequest[A] => Future[Result]
                               ) = {

    def redirectToCreateAgentServicesAccount(reason: String): Future[Result] = {
      Logger.info(s"[AuthenticatedIdentifierAction][authoriseAgent]: Agent services account required - $reason")
      Future.successful(Redirect(routes.CreateAgentServicesAccountController.onPageLoad()))
    }

    val hmrcAgentEnrolmentKey = "HMRC-AS-AGENT"
    val arnIdentifier = "AgentReferenceNumber"

    enrolments.getEnrolment(hmrcAgentEnrolmentKey).fold(
      redirectToCreateAgentServicesAccount("missing HMRC-AS-AGENT enrolment group")
    ){
      agentEnrolment =>
        agentEnrolment.getIdentifier(arnIdentifier).fold(
          redirectToCreateAgentServicesAccount("missing agent reference number")
        ){
          enrolmentIdentifier =>
            val arn = enrolmentIdentifier.value

            if(arn.isEmpty) {
              redirectToCreateAgentServicesAccount("agent reference number is empty")
            } else {
              block(IdentifierRequest(request, internalId, AffinityGroup.Agent, Some(arn)))
            }
      }
    }
  }

  private def recoverFromAuthorisation : PartialFunction[Throwable, Result] = {
    case _: NoActiveSession => Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
    case _: InsufficientEnrolments => Redirect(routes.UnauthorisedController.onPageLoad())
    case _: InsufficientConfidenceLevel => Redirect(routes.UnauthorisedController.onPageLoad())
    case _: UnsupportedAuthProvider => Redirect(routes.UnauthorisedController.onPageLoad())
    case _: UnsupportedAffinityGroup => Redirect(routes.UnauthorisedController.onPageLoad())
    case _: UnsupportedCredentialRole => Redirect(routes.UnauthorisedController.onPageLoad())
  }

  override def invokeBlock[A](request: Request[A],
                              block: IdentifierRequest[A] => Future[Result]
                             ): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    val retrievals = Retrievals.internalId and
                     Retrievals.affinityGroup and
                     Retrievals.allEnrolments

    authorised().retrieve(retrievals) {
      case Some(internalId) ~ Some(Agent) ~ enrolments =>
        authoriseAgent(request, enrolments, internalId, block)
      case Some(internalId) ~ Some(Organisation) ~ _ =>
        block(IdentifierRequest(request, internalId, AffinityGroup.Organisation))
      case Some(_) ~ _ ~ _ =>
        Future.successful(Redirect(routes.UnauthorisedController.onPageLoad()))
      case _ =>
        throw new UnauthorizedException("Unable to retrieve internal Id")
    } recover recoverFromAuthorisation
  }

}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

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
