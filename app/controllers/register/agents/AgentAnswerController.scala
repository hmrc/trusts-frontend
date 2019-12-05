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

package controllers.register.agents

import controllers.actions._
import javax.inject.Inject
import models.NormalMode
import navigation.Navigator
import pages.register.agents.AgentAnswerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.agents.AgentAnswerView

import scala.concurrent.ExecutionContext

class AgentAnswerController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       navigator: Navigator,
                                       hasAgentAffinityGroup: RequireStateActionProviderImpl,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AgentAnswerView, countryOptions : CountryOptions
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    identify andThen hasAgentAffinityGroup() andThen getData(draftId) andThen requireData

  def onPageLoad(draftId: String)= actions(draftId) {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId)

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

  def onSubmit(draftId: String) = actions(draftId) {
    implicit request =>
      Redirect(navigator.nextPage(AgentAnswerPage ,NormalMode, draftId)(request.userAnswers))
  }
}
