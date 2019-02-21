package controllers.actions

import controllers.routes
import javax.inject.Inject
import models.requests.DataRequest
import pages.QuestionPage
import play.api.libs.json.Reads
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Call, Result}

import scala.concurrent.{ExecutionContext, Future}

case class RequiredPage[A](page : QuestionPage[A],
                           redirect : Call = routes.SessionExpiredController.onPageLoad())

class RequiredPageAction[T] @Inject()(required : RequiredPage[T])
                                     (implicit val executionContext: ExecutionContext,
                                       val reads: Reads[T]) extends ActionRefiner[DataRequest, DataRequest] {

  override protected def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] = {

    request.userAnswers.get(required.page) match {
      case None =>
        Future.successful(Left(Redirect(required.redirect)))
      case Some(_) =>
        Future.successful(Right(request))
    }
  }
}

class RequiredPageActionProvider @Inject()(implicit ec: ExecutionContext) {

  def apply[T](required : RequiredPage[T])(implicit reads : Reads[T]) =
    new RequiredPageAction(required)
}