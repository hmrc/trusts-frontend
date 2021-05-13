/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register.suitability

import connector.TrustsAuthConnector
import controllers.actions.StandardActionSets
import forms.AccessCodeFormProvider
import handlers.ErrorHandler
import models.TrustsAuthAllowed
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability.NonTaxableTrustRegistrationAccessCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NonTaxableTrustRegistrationAccessCodeController @Inject()(
                                                                 override val messagesApi: MessagesApi,
                                                                 actions: StandardActionSets,
                                                                 val controllerComponents: MessagesControllerComponents,
                                                                 view: NonTaxableTrustRegistrationAccessCodeView,
                                                                 trustsAuthConnector: TrustsAuthConnector,
                                                                 formProvider: AccessCodeFormProvider,
                                                                 errorHandler: ErrorHandler
                                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[String] = formProvider()

  private val messageKeyPrefix: String = "nonTaxableTrustRegistrationAccessCode"

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() {
    implicit request =>

      Ok(view(form))
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData().async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors))),

        accessCode => {
          trustsAuthConnector.authoriseAccessCode(accessCode) map {
            case TrustsAuthAllowed(true) =>
              Redirect(controllers.register.routes.CreateDraftRegistrationController.create().url)
            case TrustsAuthAllowed(false) =>
              BadRequest(view(form.withError(FormError("value", s"$messageKeyPrefix.error.unrecognised"))))
            case _ =>
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }
        }
      )
  }
}
