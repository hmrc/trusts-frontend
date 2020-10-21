/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.register.agents

import controllers.actions._
import forms.YesNoFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.agents.RemoveDraftYesNoView

import scala.concurrent.{ExecutionContext, Future}

class RemoveDraftYesNoController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            registrationsRepository: RegistrationsRepository,
                                            standardActionSets: StandardActionSets,
                                            yesNoFormProvider: YesNoFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: RemoveDraftYesNoView
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val prefix: String = "removeDraftYesNo"

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix(prefix)

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.identifiedUserWithData(draftId)

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      clientReferenceNumber(draftId).map {
        crn => Ok(view(form, draftId, crn))
      }
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val redirect: Result = Redirect(routes.AgentOverviewController.onPageLoad())

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          clientReferenceNumber(draftId).map {
            crn => BadRequest(view(formWithErrors, draftId, crn))
          },

        value => {
          if (value) {
            registrationsRepository.removeDraft(draftId).map {
              _ => redirect
            }
          } else {
            Future.successful(redirect)
          }
        }
      )
  }

  private def clientReferenceNumber(draftId: String)(implicit request: RegistrationDataRequest[AnyContent], hc: HeaderCarrier): Future[String] = {
    val defaultText: String = request.messages(messagesApi)(s"$prefix.defaultText")
    registrationsRepository.getDraft(draftId).map {
      draft => draft.agentInternalRef.getOrElse(defaultText)
    }
  }
}
