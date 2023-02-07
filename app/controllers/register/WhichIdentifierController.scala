/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.register

import config.FrontendAppConfig
import controllers.actions.StandardActionSets
import forms.WhichIdentifierFormProvider
import models.{Enumerable, WhichIdentifier}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.WhichIdentifierView

import javax.inject.Inject

class WhichIdentifierController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           appConfig: FrontendAppConfig,
                                           actions: StandardActionSets,
                                           formProvider: WhichIdentifierFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: WhichIdentifierView
                                         ) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  private val form: Form[WhichIdentifier] = formProvider()

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() {
    implicit request =>
      Ok(view(form))
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          BadRequest(view(formWithErrors)),

        {
          case WhichIdentifier.UTRIdentifier => Redirect(appConfig.maintainATrustWithUTR)
          case WhichIdentifier.URNIdentifier => Redirect(appConfig.maintainATrustWithURN)
          case WhichIdentifier.NoIdentifier => Redirect(controllers.register.routes.RefSentByPostController.onPageLoad())
        }
      )
  }
}
