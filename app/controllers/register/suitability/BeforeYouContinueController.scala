/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register.suitability

import controllers.actions.StandardActionSets
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import navigation.registration.TaskListNavigator
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability.BeforeYouContinueView

class BeforeYouContinueController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             standardActionSets: StandardActionSets,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: BeforeYouContinueView,
                                             navigator: TaskListNavigator
                                           ) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.identifiedUserWithData(draftId)

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      Ok(view(draftId))
  }

  def onSubmit(draftId : String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      Redirect(request.affinityGroup match {
        case AffinityGroup.Agent =>
          navigator.agentDetailsJourneyUrl(draftId)
        case _ =>
          controllers.register.routes.TaskListController.onPageLoad(draftId).url
      })
  }
}
