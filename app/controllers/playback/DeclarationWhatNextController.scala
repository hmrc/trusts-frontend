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

package controllers.playback

import controllers.actions._
import forms.DeclarationWhatNextFormProvider
import javax.inject.Inject
import models.Enumerable
import navigation.VariationsNavigator
import pages.playback.DeclarationWhatNextPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.playback.DeclarationWhatNextView

import scala.concurrent.{ExecutionContext, Future}

class DeclarationWhatNextController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               sessionRepository: RegistrationsRepository,
                                               navigator: VariationsNavigator,
                                               identify: IdentifierAction,
                                               playbackAction: PlaybackAction,
                                               getData: DataRetrievalAction,
                                               requireData: DataRequiredAction,
                                               formProvider: DeclarationWhatNextFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: DeclarationWhatNextView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData andThen playbackAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(DeclarationWhatNextPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData andThen playbackAction).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DeclarationWhatNextPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.declarationWhatsNextPage(updatedAnswers))
        }
      )
  }
}
