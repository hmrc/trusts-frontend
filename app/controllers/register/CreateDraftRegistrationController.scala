/*
 * Copyright 2024 HM Revenue & Customs
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

import controllers.actions.StandardActionSets
import navigation.registration.TaskListNavigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.DraftRegistrationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CreateDraftRegistrationController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  navigator: TaskListNavigator,
  draftService: DraftRegistrationService,
  actions: StandardActionSets
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def create: Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData().async { implicit request =>
    draftService.create(request).map { draftId =>
      if (request.isAgent) {
        Redirect(navigator.agentDetailsJourneyUrl(draftId))
      } else {
        Redirect(routes.TaskListController.onPageLoad(draftId))
      }
    }
  }

}
