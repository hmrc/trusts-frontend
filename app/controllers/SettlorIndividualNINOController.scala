package controllers

import controllers.actions._
import forms.SettlorIndividualNINOFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.SettlorIndividualNINOPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.SettlorIndividualNINOView

import scala.concurrent.{ExecutionContext, Future}

class SettlorIndividualNINOController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: DataRequiredAction,
                                        formProvider: SettlorIndividualNINOFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: SettlorIndividualNINOView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SettlorIndividualNINOPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorIndividualNINOPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorIndividualNINOPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
