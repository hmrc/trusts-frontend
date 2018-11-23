package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.TrustContactPhoneNumberFormProvider
import models.Mode
import pages.TrustContactPhoneNumberPage
import utils.Navigator
import views.html.trustContactPhoneNumber

import scala.concurrent.Future

class TrustContactPhoneNumberController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: TrustContactPhoneNumberFormProvider
                                      ) extends FrontendController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode) = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustContactPhoneNumberPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(trustContactPhoneNumber(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(trustContactPhoneNumber(appConfig, formWithErrors, mode))),
        (value) => {
          val updatedAnswers = request.userAnswers.set(TrustContactPhoneNumberPage, value)

          dataCacheConnector.save(updatedAnswers.cacheMap).map(
            _ =>
              Redirect(navigator.nextPage(TrustContactPhoneNumberPage, mode)(updatedAnswers))
          )
        }
      )
  }
}
