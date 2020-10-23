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

package filters

import akka.stream.Materializer
import config.FrontendAppConfig
import handlers.ErrorHandler
import javax.inject.Inject
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc.{Filter, MessagesControllerComponents, RequestHeader, Result}
import uk.gov.hmrc.auth.otac.{OtacAuthConnector, OtacAuthorisationFunctions}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.HeaderCarrierConverter
import utils.Session

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

class CampaignWhitelistFilter @Inject()(
                                       override val mat: Materializer,
                                       val authConnector : OtacAuthConnector,
                                       appConfig: FrontendAppConfig,
                                       mcc: MessagesControllerComponents,
                                       errorHandler : ErrorHandler
                                       )
  extends Filter with OtacAuthorisationFunctions {

  private val logger: Logger = Logger(getClass)

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {

    import play.api.http.Status._

    val registerPrefix = rh.path.contains(register.RoutesPrefix.prefix)

    if (appConfig.campaignWhitelistEnabled && registerPrefix) {

      rh.session.get(SessionKeys.otacToken)
        .orElse(rh.queryString.get("p").flatMap(_.headOption))
        .orElse(rh.cookies.get("whitelisting").map(_.value))
        .map {
          token =>

            implicit val hc : HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(rh.headers, Some(rh.session))

            logger.info(s"[Session ID: ${Session.id(hc)}] token retrieved $token")

            withVerifiedPasscode("trusts", Some(token)){
              f(rh)
            }.recover {
              case NonFatal(e) =>

                logger.info(s"[Session ID: ${Session.id(hc)}] Not authorised to access Trusts ${e.getMessage}")

                Redirect(s"${appConfig.otacUrl}?p=$token")
                .addingToSession(
                  SessionKeys.redirect -> s"${appConfig.loginContinueUrl}?p=$token",
                  SessionKeys.otacToken -> token
                )(rh)
            }
        }.getOrElse(errorHandler.onClientError(rh, NOT_FOUND))

    } else {
      f(rh)
    }
  }

}
