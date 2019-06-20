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

package controllers

import controllers.actions.IdentifierAction
import javax.inject.Inject
import models.NormalMode
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CreateDraftRegistrationService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

class CreateDraftRegistrationController @Inject()(
                                                   val controllerComponents: MessagesControllerComponents,
                                                   draftService : CreateDraftRegistrationService,
                                                   identify: IdentifierAction
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def create: Action[AnyContent] = identify.async { implicit request =>
    draftService.create(request).map {
      draftId =>
        Redirect(routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, draftId))
    }
  }

}
