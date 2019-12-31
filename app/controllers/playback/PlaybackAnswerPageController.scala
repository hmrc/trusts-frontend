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
import controllers.actions.playback.PlaybackIdentifierAction
import controllers.actions.register.{RegistrationDataRequiredAction, RegistrationDataRetrievalAction, RegistrationIdentifierAction}
import javax.inject.Inject
import models.playback.http.Processed
import pages.playback.WhatIsTheUTRVariationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import utils.PrintPlaybackHelper
import views.html.playback.PlaybackAnswersView

import scala.concurrent.{ExecutionContext, Future}

class PlaybackAnswerPageController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              identify: RegistrationIdentifierAction,
                                              playbackAction: PlaybackIdentifierAction,
                                              getData: RegistrationDataRetrievalAction,
                                              requireData: RegistrationDataRequiredAction,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: PlaybackAnswersView,
                                              countryOptions: CountryOptions,
                                              printPlaybackAnswersHelper: PrintPlaybackHelper,
                                              trustConnector: TrustConnector
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions =
    identify andThen getData andThen requireData andThen playbackAction

  def onPageLoad() = actions.async {
    implicit request =>

      request.userAnswers.get(WhatIsTheUTRVariationPage) match {
        case Some(utr) =>
          trustConnector.playback(utr) map {
            case Processed(trust, _) =>

              val sections = printPlaybackAnswersHelper.summary(trust)

              Ok(view(sections))

            case _ => ???

          }

        case _ => Future.successful(Redirect(controllers.register.routes.SessionExpiredController.onPageLoad()))

      }

  }

}