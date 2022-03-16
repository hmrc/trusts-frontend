/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.register

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import controllers.actions.register.RegistrationIdentifierAction
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session

import scala.concurrent.ExecutionContext

@Singleton
class LogoutController @Inject()(
                                  appConfig: FrontendAppConfig,
                                  auditConnector: AuditConnector,
                                  identify: RegistrationIdentifierAction,
                                  val controllerComponents: MessagesControllerComponents)
                                (implicit val ec: ExecutionContext) extends FrontendBaseController {

  def logout: Action[AnyContent] = identify {
    request =>

      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      if(appConfig.logoutAudit) {

        val auditData = Map(
          "sessionId" -> Session.id(hc),
          "event" -> "signout",
          "service" -> "trusts-frontend",
          "userGroup" -> request.affinityGroup.toString
        )

        val auditWithAgent = request.agentARN.fold(auditData) { arn =>
          auditData ++ Map("agentReferenceNumber" -> arn)
        }

        auditConnector.sendExplicitAudit(
          "trusts",
          auditWithAgent
        )

      }

      Redirect(appConfig.logoutUrl).withSession(session = ("feedbackId", Session.id(hc)))
  }
}
