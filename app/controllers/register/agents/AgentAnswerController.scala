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

package controllers.register.agents

import controllers.actions._
import javax.inject.Inject
import models.NormalMode
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.agents.AgentAnswerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import utils.{CheckYourAnswersHelper, DateFormatter}
import viewmodels.AnswerSection
import views.html.register.agents.AgentAnswerView

class AgentAnswerController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       navigator: Navigator,
                                       actionSet: AgentActionSets,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AgentAnswerView,
                                       countryOptions : CountryOptions,
                                       dateFormatter: DateFormatter
                                     ) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    actionSet.identifiedUserWithData(draftId)

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, dateFormatter)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.agentInternalReference,
            checkYourAnswersHelper.agentName,
            checkYourAnswersHelper.agentAddressYesNo,
            checkYourAnswersHelper.agentUKAddress,
            checkYourAnswersHelper.agentInternationalAddress,
            checkYourAnswersHelper.agenciesTelephoneNumber
          ).flatten
        )
      )

      Ok(view(draftId ,sections))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>
      Redirect(navigator.nextPage(AgentAnswerPage ,NormalMode, draftId)(request.userAnswers))
  }
}
