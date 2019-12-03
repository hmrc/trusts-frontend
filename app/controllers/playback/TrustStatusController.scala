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
import pages.WhatIsTheUTRVariationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.{PlaybackRepository, RegistrationsRepository}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.playback.status._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

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
                                       playbackExtractor: UserAnswersExtractor,
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

  def checkIfLocked(utr: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    trustStoreConnector.get(request.internalId, utr).flatMap {
      case Some(claim) if claim.trustLocked =>
        Future.successful(Redirect(routes.TrustStatusController.locked()))
      case _ =>
        tryToPlayback(utr)
    }
  }

  def tryToPlayback(utr: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    trustConnector.playback(utr) flatMap {
      case Closed => Future.successful(Redirect(routes.TrustStatusController.closed()))
      case Processing => Future.successful(Redirect(routes.TrustStatusController.processing()))
      case UtrNotFound => Future.successful(Redirect(routes.TrustStatusController.notFound()))
      case Processed(playback, _) => extract(utr, playback)
      case _ => Future.successful(Redirect(routes.TrustStatusController.down()))
    }
  }

  def extract(utr: String, playback: GetTrust)(implicit request: DataRequest[AnyContent]) : Future[Result] = {
    // Todo create new getData, requireData to return instance of PlaybackData for all Variations controllers rather than calling request.userAnswers.toPlaybackUserAnswers
    playbackExtractor.extract(request.userAnswers.toPlaybackUserAnswers, playback) match {
      case Right(Success(answers)) =>
        playbackRepository.store(answers) map { _ =>

          request.affinityGroup match {
            case Agent =>
              Redirect(routes.InformationMaintainingThisTrustController.onPageLoad())
            case _ =>
              Redirect(config.claimATrustUrl(utr))
          }
        }
      case Right(Failure(reason)) =>
        // Todo update where it goes and log reason?
        Future.successful(Redirect(routes.TrustStatusController.down()))
      case Left(reason) =>
        // Todo update where it goes and log reason?
        Future.successful(Redirect(routes.TrustStatusController.down()))
    }
  }

  def enforceUtr()(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(WhatIsTheUTRVariationPage) match {
      case None => Future.successful(Redirect(routes.WhatIsTheUTRVariationsController.onPageLoad()))
      case Some(utr) => block(utr)
    }
  }
}
