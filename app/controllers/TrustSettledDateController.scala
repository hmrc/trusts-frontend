/*
 * Copyright 2018 HM Revenue & Customs
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

import java.time.LocalDate
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
import views.html.date
import controllers.routes.TrustSettledDateController

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


  val messageKeyPrefix = "trustSettledDate"
  val form = formProvider(messageKeyPrefix)

  def actionRoute(mode: Mode) = TrustSettledDateController.onSubmit(mode)

  def onPageLoad(mode: Mode) = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustSettledDatePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(date(appConfig, preparedForm, mode,None,actionRoute(mode),messageKeyPrefix))
  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[LocalDate]) =>
          Future.successful(BadRequest(date(appConfig, formWithErrors, mode,None,actionRoute(mode),messageKeyPrefix))),
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
