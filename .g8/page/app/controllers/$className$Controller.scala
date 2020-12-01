package controllers

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

class $className$Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: RegistrationIdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: RegistrationDataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: $className$View
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    implicit request =>
      Ok(view())
  }
}
