package controllers

import controllers.actions._
import forms.IndividualBeneficiaryAddressUKFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.IndividualBeneficiaryAddressUKPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.IndividualBeneficiaryAddressUKView

import scala.concurrent.{ExecutionContext, Future}

class IndividualBeneficiaryAddressUKController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionRepository: SessionRepository,
                                      navigator: Navigator,
                                      identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      formProvider: IndividualBeneficiaryAddressUKFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: IndividualBeneficiaryAddressUKView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(IndividualBeneficiaryAddressUKPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualBeneficiaryAddressUKPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IndividualBeneficiaryAddressUKPage, mode)(updatedAnswers))
        }
      )
  }
}
