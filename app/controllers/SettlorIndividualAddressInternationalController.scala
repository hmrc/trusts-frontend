package controllers

import controllers.actions._
import forms.SettlorIndividualAddressInternationalFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.SettlorIndividualAddressInternationalPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.SettlorIndividualAddressInternationalView

import scala.concurrent.{ExecutionContext, Future}

class SettlorIndividualAddressInternationalController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionRepository: SessionRepository,
                                      navigator: Navigator,
                                      identify: IdentifierAction,
                                      getData: DraftIdRetrievalActionProvider,
                                      requireData: DataRequiredAction,
                                      formProvider: SettlorIndividualAddressInternationalFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: SettlorIndividualAddressInternationalView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SettlorIndividualAddressInternationalPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorIndividualAddressInternationalPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorIndividualAddressInternationalPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
