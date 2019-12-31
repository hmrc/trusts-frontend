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

import config.FrontendAppConfig
import connector.{TrustConnector, TrustsStoreConnector}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import handlers.ErrorHandler
import javax.inject.Inject
import mapping.playback.UserAnswersExtractor
import models.playback.http._
import models.requests.DataRequest
import navigation.Navigator
import pages.playback.WhatIsTheUTRVariationPage
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.{PlaybackRepository, RegistrationsRepository}
import services.PlaybackAuthenticationService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.playback.status._

import scala.concurrent.{ExecutionContext, Future}

class TrustStatusController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       playbackRepository: PlaybackRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       closedView: ClosedErrorView,
                                       stillProcessingView: StillProcessingErrorView,
                                       doesNotMatchView: DoesNotMatchErrorView,
                                       ivDownView: IVDownView,
                                       trustConnector: TrustConnector,
                                       trustStoreConnector: TrustsStoreConnector,
                                       config: FrontendAppConfig,
                                       errorHandler: ErrorHandler,
                                       lockedView: TrustLockedView,
                                       alreadyClaimedView: TrustAlreadyClaimedView,
                                       playbackProblemContactHMRCView: PlaybackProblemContactHMRCView,
                                       playbackExtractor: UserAnswersExtractor,
                                       authenticationService: PlaybackAuthenticationService,
                                       val controllerComponents: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def closed(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(closedView(request.affinityGroup, utr)))
      }
  }

  def processing(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(stillProcessingView(request.affinityGroup, utr)))
      }
  }

  def sorryThereHasBeenAProblem(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(playbackProblemContactHMRCView(utr)))
      }
  }

  def notFound(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      enforceUtr() { _ =>
        Future.successful(Ok(doesNotMatchView(request.affinityGroup)))
      }
  }

  def locked(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(lockedView(utr)))
      }
  }

  def down(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      enforceUtr() { _ =>
        Future.successful(ServiceUnavailable(ivDownView(request.affinityGroup)))
      }
  }

  def alreadyClaimed(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(alreadyClaimedView(utr)))
      }
  }

  def status(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      enforceUtr() { utr =>
        checkIfLocked(utr)
      }
  }

  private def checkIfLocked(utr: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    trustStoreConnector.get(request.internalId, utr).flatMap {
      case Some(claim) if claim.trustLocked =>
        Future.successful(Redirect(controllers.playback.routes.TrustStatusController.locked()))
      case _ =>
        tryToPlayback(utr)
    }
  }

  private def tryToPlayback(utr: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    trustConnector.playback(utr) flatMap {
      case Closed => Future.successful(Redirect(controllers.playback.routes.TrustStatusController.closed()))
      case Processing => Future.successful(Redirect(controllers.playback.routes.TrustStatusController.processing()))
      case UtrNotFound => Future.successful(Redirect(controllers.playback.routes.TrustStatusController.notFound()))
      case Processed(playback, _) =>
        authenticationService.authenticate(utr) flatMap {
          case Left(failure) => Future.successful(failure)
          case Right(_) => extract(utr, playback)
        }
      case SorryThereHasBeenAProblem => Future.successful(Redirect(routes.TrustStatusController.sorryThereHasBeenAProblem()))
      case _ => Future.successful(Redirect(routes.TrustStatusController.down()))
    }
  }

  private def extract(utr: String, playback: GetTrust)(implicit request: DataRequest[AnyContent]) : Future[Result] = {
    // Todo create new getData, requireData to return instance of PlaybackData for all Variations controllers rather than calling request.userAnswers.toPlaybackUserAnswers
    playbackExtractor.extract(request.userAnswers.toPlaybackUserAnswers, playback) match {
      case Right(answers) =>
        playbackRepository.store(answers) map { _ =>
          Redirect(routes.InformationMaintainingThisTrustController.onPageLoad())
        }
      case Left(reason) =>
        Logger.warn(s"[TrustStatusController] unable to extract user answers due to $reason")
        Future.successful(Redirect(routes.TrustStatusController.sorryThereHasBeenAProblem()))
    }
  }

  private def enforceUtr()(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(WhatIsTheUTRVariationPage) match {
      case None => Future.successful(Redirect(routes.WhatIsTheUTRVariationsController.onPageLoad()))
      case Some(utr) => block(utr)
    }
  }
}
