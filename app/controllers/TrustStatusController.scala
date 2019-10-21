package controllers

import connector.TrustConnector
import controllers.actions.{DataRequiredAction, DraftIdRetrievalActionProvider, IdentifierAction, RequiredAnswer}
import javax.inject.Inject
import models.{Closed, NormalMode, Processing}
import models.requests.DataRequest
import navigation.Navigator
import pages.{IndividualBeneficiaryNamePage, WhatIsTheUTRVariationPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.Gettable
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.{CannotAccessErrorView, DoesNotMatchErrorView, StillProcessingErrorView}

import scala.concurrent.{ExecutionContext, Future}

class TrustStatusController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       cannotAccessView: CannotAccessErrorView,
                                       stillProcessingView: StillProcessingErrorView,
                                       doesNotMatchView: DoesNotMatchErrorView,
                                       trustConnector: TrustConnector,
                                       val controllerComponents: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  def closed(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(Ok(cannotAccessView(utr)))
      }
  }

  def processing(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(Ok(stillProcessingView(utr)))
      }
  }

  def notFound(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        Future.successful(Ok(doesNotMatchView(utr)))
      }
  }

  def onError(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      enforceUtr(draftId) { utr =>
        trustConnector.getTrustStatus(utr) map {
          case Closed => Redirect(routes.TrustStatusController.closed(draftId))
          case Processing => Redirect(routes.TrustStatusController.processing(draftId))
          case NotFound => Redirect(routes.TrustStatusController.notFound(draftId))
        }
      }
  }


  def enforceUtr[A](draftId: String)(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(WhatIsTheUTRVariationPage) match {
      case None => Future.successful(Redirect(routes.WhatIsTheUTRVariationsController.onPageLoad(NormalMode, draftId)))
      case Some(utr) => block(utr)
    }
  }
}
