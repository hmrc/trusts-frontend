/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.actions.playback.{PlaybackDataRequiredAction, PlaybackDataRetrievalAction, PlaybackIdentifierAction}
import controllers.actions.register.RegistrationIdentifierAction
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.print.playback.PrintPlaybackHelper
import views.html.playback.PlaybackAnswersView

import scala.concurrent.{ExecutionContext, Future}

class PlaybackAnswerPageController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              identify: RegistrationIdentifierAction,
                                              playbackIdentify: PlaybackIdentifierAction,
                                              getData: PlaybackDataRetrievalAction,
                                              requireData: PlaybackDataRequiredAction,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: PlaybackAnswersView,
                                              printPlaybackAnswersHelper: PrintPlaybackHelper
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions =
    identify andThen getData andThen requireData andThen playbackIdentify

  def onPageLoad() = actions.async {
    implicit request =>

      val sections = printPlaybackAnswersHelper.summary(request.userAnswers)
      val nonAmendSections = printPlaybackAnswersHelper.nonAmendSections(request.userAnswers)

      Future.successful(Ok(view(sections, nonAmendSections)))
  }

}