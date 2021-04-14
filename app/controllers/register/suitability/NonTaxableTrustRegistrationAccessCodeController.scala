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
import models.requests.RegistrationDataRequest
import models.{TrustsAuthAllowed, TrustsAuthDenied, TrustsAuthInternalServerError}
import navigation.registration.TaskListNavigator
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability.NonTaxableTrustRegistrationAccessCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NonTaxableTrustRegistrationAccessCodeController @Inject()(
                                                                 override val messagesApi: MessagesApi,
                                                                 standardActionSets: StandardActionSets,
                                                                 val controllerComponents: MessagesControllerComponents,
                                                                 view: NonTaxableTrustRegistrationAccessCodeView,
                                                                 navigator: TaskListNavigator,
                                                                 trustsAuthConnector: TrustsAuthConnector,
                                                                 formProvider: AccessCodeFormProvider,
                                                                 errorHandler: ErrorHandler
                                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[String] = formProvider()

  private val messageKeyPrefix: String = "nonTaxableTrustRegistrationAccessCode"

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.identifiedUserWithData(draftId)

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      Ok(view(form, draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId))),

        accessCode => {
          trustsAuthConnector.authoriseAccessCode(draftId, accessCode) map {
            case TrustsAuthAllowed(_) => request.affinityGroup match {
              case AffinityGroup.Agent =>
                Redirect(navigator.agentDetailsJourneyUrl(draftId))
              case _ =>
                Redirect(controllers.register.routes.TaskListController.onPageLoad(draftId).url)
            }
            case TrustsAuthDenied(_) =>
              BadRequest(view(form.withError(FormError("value", s"$messageKeyPrefix.error.unrecognised")), draftId))
            case TrustsAuthInternalServerError =>
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }
        }
      )
  }
}
