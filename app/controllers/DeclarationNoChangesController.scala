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
import forms.DeclarationChangesNoChangesFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.{DeclarationChangesNoChangesPage, DeclarationWhatNextPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.DeclarationChangesNoChangesView

import scala.concurrent.{ExecutionContext, Future}

class DeclarationNoChangesController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       requiredAnswer: RequiredAnswerActionProvider,
                                       formProvider: DeclarationChangesNoChangesFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DeclarationChangesNoChangesView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def actions() = identify andThen getData andThen requireData andThen
      requiredAnswer(RequiredAnswer(DeclarationWhatNextPage, routes.DeclarationWhatNextController.onPageLoad()))

  def onPageLoad(): Action[AnyContent] = actions() {
    implicit request =>

      val preparedForm = request.userAnswers.get(DeclarationChangesNoChangesPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.affinityGroup, routes.DeclarationNoChangesController.onSubmit()))
  }

  def onSubmit(): Action[AnyContent] = actions().async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, request.affinityGroup, routes.DeclarationNoChangesController.onSubmit()))),

        // TODO:  Check response for submission of no change data and redirect accordingly

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DeclarationChangesNoChangesPage, value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(routes.DeclarationNoChangesController.onPageLoad()) // TODO Redirect to variation confirmation page
        }
      )

  }

}
