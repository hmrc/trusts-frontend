package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.TrustSettledDateFormProvider
import models.Mode
import pages.TrustSettledDatePage
import utils.Navigator
import views.html.trustSettledDate

import scala.concurrent.Future

class TrustSettledDateController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: TrustSettledDateFormProvider
                                      ) extends FrontendController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode) = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustSettledDatePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(trustSettledDate(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(trustSettledDate(appConfig, formWithErrors, mode))),
        (value) => {
          val updatedAnswers = request.userAnswers.set(TrustSettledDatePage, value)

          dataCacheConnector.save(updatedAnswers.cacheMap).map(
            _ =>
              Redirect(navigator.nextPage(TrustSettledDatePage, mode)(updatedAnswers))
          )
        }
      )
  }
}
