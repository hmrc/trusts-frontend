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
import models.requests.IdentifierRequest
import play.api.Logger
import play.api.mvc.Results.Redirect
import play.api.mvc.{Request, Result, _}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}


class IdentifyForPlayback @Inject()(utr: String,
                                    override val authConnector: AuthConnector,
                                    val parser: BodyParsers.Default,
                                    trustsAuth: TrustsAuth
                             )(override implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest] with AuthorisedFunctions {



  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised(Relationship(trustsAuth.config.relationshipName, Set(BusinessKey(trustsAuth.config.relationshipIdentifier, utr)))) {
      request match {
        case req: IdentifierRequest[A] => block(req)
        case _ => Future.successful(trustsAuth.redirectToLogin)
      }
    } recoverWith {
      case FailedRelationship(msg) =>
        // relationship does not exist
        Logger.info(s"[IdentifyForPlayback] Relationship does not exist in Trust IV for user due to $msg")
        request match {
          case _: IdentifierRequest[A] => Future.successful(Redirect(controllers.routes.IndexController.onPageLoad()))
          case _ => Future.successful(trustsAuth.redirectToLogin)
        }
    }
  }

  override protected def composeAction[A](action: Action[A]): Action[A] = new AuthenticatedIdentifierAction(action, trustsAuth)
}


class IdentifyForRegistration @Inject()(utr: String,
                                        val parser: BodyParsers.Default,
                                        trustsAuth: TrustsAuth
                                   )(override implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifierRequest, AnyContent]  {

  def composedAuthAction[A](action: Action[A]) = new AuthenticatedIdentifierAction(action, trustsAuth)

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    request match {
      case req: IdentifierRequest[A] =>
        block(req)
      case _ =>
        Future.successful(trustsAuth.redirectToLogin)
    }
  }

  override def composeAction[A](action: Action[A]): Action[A] = composedAuthAction(action)
}
