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

import controllers.actions._
import javax.inject.Inject
import models.NormalMode
import navigation.Navigator
import pages.AgentAnswerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import play.mvc.BodyParser.AnyContent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.AgentAnswerView

import scala.concurrent.ExecutionContext

class AgentAnswerController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       navigator: Navigator,
                                       hasAgentAffinityGroup: RequireStateActionProviderImpl,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AgentAnswerView, countryOptions : CountryOptions
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions =
    identify andThen hasAgentAffinityGroup() andThen getData andThen requireData

  def onPageLoad= actions {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers)

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

      Ok(view(sections))
  }

  def onSubmit = actions {
    implicit request =>
      Redirect(navigator.nextPage(AgentAnswerPage ,NormalMode)(request.userAnswers))
  }
}
