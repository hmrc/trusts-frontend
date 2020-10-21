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
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.agents.RemoveTrustYesNoView

import scala.concurrent.{ExecutionContext, Future}

class RemoveTrustYesNoController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            registrationsRepository: RegistrationsRepository,
                                            standardActionSets: StandardActionSets,
                                            yesNoFormProvider: YesNoFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: RemoveTrustYesNoView
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("removeTrustYesNo")

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

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          clientReferenceNumber(draftId).map {
            crn => BadRequest(view(formWithErrors, draftId, crn))
          },

        value => {
          if (value) {
            registrationsRepository.removeDraft(draftId).map {
              _ => Redirect(routes.AgentOverviewController.onPageLoad())
            }
          } else {
            Future.successful(Redirect(routes.AgentOverviewController.onPageLoad()))
          }
        }
      )
  }

  private def clientReferenceNumber(draftId: String)(implicit hc: HeaderCarrier): Future[String] = {
    val defaultText: String = "the trust"
    registrationsRepository.getDraft(draftId).map {
      draft => draft.agentInternalRef.getOrElse(defaultText)
    }
  }
}
