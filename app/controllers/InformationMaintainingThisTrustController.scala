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

import config.FrontendAppConfig
import controllers.actions.{DataRequiredAction, DraftIdRetrievalActionProvider, IdentifierAction}
import javax.inject.Inject
import models.NormalMode
import navigation.Navigator
import pages.WhatIsTheUTRPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.InformationMaintainingThisTrustView

import scala.concurrent.ExecutionContext

class InformationMaintainingThisTrustController @Inject()(
                                                           sessionRepository: SessionRepository,
                                                           navigator: Navigator,
                                                           identify: IdentifierAction,
                                                           getData: DraftIdRetrievalActionProvider,
                                                           requireData: DataRequiredAction,
                                                           val controllerComponents: MessagesControllerComponents,
                                                           view: InformationMaintainingThisTrustView
                                                         )(implicit ec: ExecutionContext,
                                                           config: FrontendAppConfig) extends FrontendBaseController with I18nSupport {

  def onPageLoad(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    implicit request =>
      request.userAnswers.get(WhatIsTheUTRPage) match {
        case Some(utr) => Ok(view(draftId, utr))
        case None => Redirect(routes.WhatIsTheUTRController.onPageLoad(NormalMode, draftId))
      }
  }
}
