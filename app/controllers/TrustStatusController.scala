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
import connector.TrustConnector
import controllers.actions.{DataRequiredAction, DraftIdRetrievalActionProvider, IdentifierAction}
import handlers.ErrorHandler
import javax.inject.Inject
import models.requests.DataRequest
import models.{Closed, NormalMode, Processed, Processing, UtrNotFound}
import navigation.Navigator
import pages.WhatIsTheUTRVariationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.{ClosedErrorView, DoesNotMatchErrorView, IVDownView, StillProcessingErrorView}

import scala.concurrent.{ExecutionContext, Future}

class TrustStatusController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       closedView: ClosedErrorView,
                                       stillProcessingView: StillProcessingErrorView,
                                       doesNotMatchView: DoesNotMatchErrorView,
                                       ivDownView: IVDownView,
                                       trustConnector: TrustConnector,
                                       config: FrontendAppConfig,
                                       errorHandler: ErrorHandler,
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

  def down(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(ServiceUnavailable(ivDownView(draftId, request.affinityGroup)))
      }
  }

  def status(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        trustConnector.getTrustStatus(utr) map {
          case Closed => Redirect(routes.TrustStatusController.closed(draftId))
          case Processing => Redirect(routes.TrustStatusController.processing(draftId))
          case UtrNotFound => Redirect(routes.TrustStatusController.notFound(draftId))
          case Processed => Redirect(config.claimATrustUrl(utr))
          case _ => Redirect(routes.TrustStatusController.down(draftId))
        }
      }
  }

  def enforceUtr(draftId: String)(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(WhatIsTheUTRVariationPage) match {
      case None => Future.successful(Redirect(routes.WhatIsTheUTRVariationsController.onPageLoad(NormalMode, draftId)))
      case Some(utr) => block(utr)
    }
  }
}
