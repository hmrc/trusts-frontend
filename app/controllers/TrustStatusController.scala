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

import config.FrontendAppConfig
import connector.{TrustConnector, TrustsStoreConnector}
import controllers.actions.{DataRequiredAction, DraftIdRetrievalActionProvider, IdentifierAction}
import handlers.ErrorHandler
import javax.inject.Inject
import models.NormalMode
import models.playback.{Closed, Processed, Processing, UtrNotFound}
import models.requests.DataRequest
import navigation.Navigator
import pages.WhatIsTheUTRVariationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.{PlaybackRepository, RegistrationsRepository}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.{ClosedErrorView, DoesNotMatchErrorView, IVDownView, StillProcessingErrorView, TrustLockedView}

import scala.concurrent.{ExecutionContext, Future}

class TrustStatusController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       playbackRepository: PlaybackRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
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
                                       val controllerComponents: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  def closed(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(Ok(closedView(draftId, request.affinityGroup, utr)))
      }
  }

  def processing(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(Ok(stillProcessingView(draftId, request.affinityGroup, utr)))
      }
  }

  def notFound(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(Ok(doesNotMatchView(draftId, request.affinityGroup)))
      }
  }

  def locked(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(Ok(lockedView(utr)))
      }
  }

  def down(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(ServiceUnavailable(ivDownView(draftId, request.affinityGroup)))
      }
  }

  def status(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        checkIfLocked(draftId, utr)
      }
  }

  def checkIfLocked(draftId: String, utr: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    trustStoreConnector.get(request.internalId, utr).flatMap {
      case Some(claim) if claim.trustLocked =>
        Future.successful(Redirect(routes.TrustStatusController.locked(draftId)))
      case _ =>
        tryToPlayback(draftId, utr)
    }
  }

  def tryToPlayback(draftId: String, utr: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    trustConnector.playback(utr) flatMap {
      case Closed => Future.successful(Redirect(routes.TrustStatusController.closed(draftId)))
      case Processing => Future.successful(Redirect(routes.TrustStatusController.processing(draftId)))
      case UtrNotFound => Future.successful(Redirect(routes.TrustStatusController.notFound(draftId)))
      case Processed(playback) =>
        playbackRepository.store(request.internalId, playback) map { _ =>
          Redirect(config.claimATrustUrl(utr))
        }
      case _ => Future.successful(Redirect(routes.TrustStatusController.down(draftId)))
    }
  }

  def enforceUtr(draftId: String)(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(WhatIsTheUTRVariationPage) match {
      case None => Future.successful(Redirect(routes.WhatIsTheUTRVariationsController.onPageLoad(NormalMode, draftId)))
      case Some(utr) => block(utr)
    }
  }
}
