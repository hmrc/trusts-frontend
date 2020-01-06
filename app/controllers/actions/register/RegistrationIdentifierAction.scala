/*
 * Copyright 2020 HM Revenue & Customs
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

import com.google.inject.Inject
import controllers.actions.{AffinityGroupIdentifierAction, TrustsAuthorisedFunctions}
import models.requests.IdentifierRequest
import play.api.mvc.{Request, Result, _}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}


class RegistrationIdentifierAction @Inject()(val parser: BodyParsers.Default,
                                             trustsAuth: TrustsAuthorisedFunctions)
                                            (override implicit val executionContext: ExecutionContext) extends ActionBuilder[IdentifierRequest, AnyContent] {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    request match {
      case req: IdentifierRequest[A] =>
        block(req)
      case _ =>
        Future.successful(trustsAuth.redirectToLogin)
    }
  }

  override def composeAction[A](action: Action[A]): Action[A] = new AffinityGroupIdentifierAction(action, trustsAuth)
}
