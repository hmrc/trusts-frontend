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
import forms.AgentInternalReferenceFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.register.agents.{AgentARNPage, AgentInternalReferencePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.agents.AgentInternalReferenceView

import scala.concurrent.{ExecutionContext, Future}

class AgentInternalReferenceController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  registrationsRepository: RegistrationsRepository,
                                                  navigator: Navigator,
                                                  formProvider: AgentInternalReferenceFormProvider,
                                                  actionSet: AgentActionSets,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: AgentInternalReferenceView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {



  def actions(draftId: String)= actionSet.identifiedUserWithData(draftId)

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AgentInternalReferencePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AgentInternalReferencePage, value))
            updatedAnswersWithARN <- Future.fromTry(updatedAnswers.set(AgentARNPage, request.agentARN.getOrElse("")))
            _              <- registrationsRepository.set(updatedAnswersWithARN)
          } yield Redirect(navigator.nextPage(AgentInternalReferencePage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
