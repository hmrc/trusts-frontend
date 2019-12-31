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

import connector.TrustConnector
import controllers.actions._
import forms.DeclarationFormProvider
import javax.inject.Inject
import models.playback.http.Processed
import navigation.Navigator
import pages.playback.{DeclarationWhatNextPage, WhatIsTheUTRVariationPage}
import pages.register.DeclarationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.PrintPlaybackHelper
import views.html.playback.DeclarationView

import scala.concurrent.{ExecutionContext, Future}

class DeclarationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       playbackAction: PlaybackIdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       requiredAnswer: RequiredAnswerActionProvider,
                                       formProvider: DeclarationFormProvider,
                                       connector: TrustConnector,
                                       playBackHelper: PrintPlaybackHelper,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DeclarationView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def actions() = identify andThen getData andThen requireData andThen playbackAction andThen
    requiredAnswer(RequiredAnswer(DeclarationWhatNextPage, routes.DeclarationWhatNextController.onPageLoad()))

  def onPageLoad(): Action[AnyContent] = actions().async {
    implicit request =>

      request.userAnswers.get(WhatIsTheUTRVariationPage) match {
        case Some(utr) => connector.playback(utr) map {
          case Processed(trust, _) =>

            val preparedForm = request.userAnswers.get(DeclarationPage) match {
              case None => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, request.affinityGroup, playBackHelper.summary(trust), controllers.playback.routes.DeclarationController.onSubmit()))

          case _ => ???
        }
        case _ => Future.successful(Redirect(controllers.register.routes.SessionExpiredController.onPageLoad()))
      }

  }

  def onSubmit(): Action[AnyContent] = actions().async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>

          request.userAnswers.get(WhatIsTheUTRVariationPage) match {
            case Some(utr) =>
              connector.playback(utr) map {
                case Processed(trust, _) =>

                  BadRequest(view(formWithErrors, request.affinityGroup, playBackHelper.summary(trust), controllers.playback.routes.DeclarationController.onSubmit()))

                case _ => ???

              }

            case _ => Future.successful(Redirect(controllers.register.routes.SessionExpiredController.onPageLoad()))

          },

        // TODO:  Check response for submission of no change data and redirect accordingly

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DeclarationPage, value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(controllers.playback.routes.VariationsConfirmationController.onPageLoad())
        }
      )

  }

}
