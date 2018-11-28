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

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.InternationalAddressFormProvider
import models.Mode
import pages.{TrustsAddressInternationalPage}
import utils.Navigator
import views.html.internationalAddress
import controllers.routes.TrustsAddressInternationalController

import scala.concurrent.Future

class TrustsAddressInternationalController @Inject()(appConfig: FrontendAppConfig,
                                                     override val messagesApi: MessagesApi,
                                                     dataCacheConnector: DataCacheConnector,
                                                     navigator: Navigator,
                                                     identify: IdentifierAction,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     formProvider: InternationalAddressFormProvider
                                      ) extends FrontendController with I18nSupport {

  val form = formProvider()
  val messagePreFix: String = "internationalAddress"

  def actionRoute(mode: Mode) = TrustsAddressInternationalController.onSubmit(mode)

  def onPageLoad(mode: Mode) = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustsAddressInternationalPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(internationalAddress(appConfig, preparedForm, mode, actionRoute(mode), messagePreFix))

  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
         Future.successful(BadRequest(internationalAddress(appConfig, formWithErrors, mode, actionRoute(mode), messagePreFix))),

        (value) => {
          val updatedAnswers = request.userAnswers.set(TrustsAddressInternationalPage, value)

          dataCacheConnector.save(updatedAnswers.cacheMap).map(
            _ =>
              Redirect(navigator.nextPage(TrustsAddressInternationalPage, mode)(updatedAnswers))

          )
        }
      )
  }
}
