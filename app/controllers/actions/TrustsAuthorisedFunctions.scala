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

package controllers.actions

import config.FrontendAppConfig
import javax.inject.Inject
import play.api.Logger
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisationException, AuthorisedFunctions, NoActiveSession}

class TrustsAuthorisedFunctions @Inject()(override val authConnector: AuthConnector,
                                          val config: FrontendAppConfig) extends AuthorisedFunctions {
  private val logger: Logger = Logger(getClass)

  def recoverFromAuthorisation : PartialFunction[Throwable, Result] = {
    case _: NoActiveSession => redirectToLogin
    case _: AuthorisationException => Redirect(controllers.register.routes.UnauthorisedController.onPageLoad())
  }

  def redirectToLogin: Result = {
    logger.debug("Redirecting to Login")
    Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
  }
}
