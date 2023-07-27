/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.abTestingUseOnly

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.register.RegistrationIdentifierAction
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session
import views.html.abTestingUseOnly.TestSignOutView

import scala.concurrent.{ExecutionContext, Future}

class TestSignOutController @Inject()(
                                       val controllerComponents: MessagesControllerComponents,
                                       identify: RegistrationIdentifierAction,
                                       view: TestSignOutView,
                                       appConfig: FrontendAppConfig,
                                       auditConnector: AuditConnector
                                     )(implicit val ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] = identify.async {
    implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      if (appConfig.logoutAudit) {

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

      logger.info(s"[TestSignOutController][onPageLoad][Session ID: ${Session.id(hc)}] Displaying TestSignOutView for A/B Testing")
      Future.successful(Ok(view()))
  }
}
