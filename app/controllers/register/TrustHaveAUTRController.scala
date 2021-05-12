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

package controllers.register

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.YesNoFormProvider

import javax.inject.Inject
import models.Mode
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.TrustHaveAUTRPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.FeatureFlagService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.TrustHaveAUTRView

import scala.concurrent.{ExecutionContext, Future}

class TrustHaveAUTRController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         registrationsRepository: RegistrationsRepository,
                                         navigator: Navigator,
                                         identify: RegistrationIdentifierAction,
                                         getData: DraftIdRetrievalActionProvider,
                                         requireData: RegistrationDataRequiredAction,
                                         formProvider: YesNoFormProvider,
                                         featureFlagService: FeatureFlagService,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: TrustHaveAUTRView
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] = identify andThen getData(draftId) andThen requireData

  val form: Form[Boolean] = formProvider.withPrefix("trustHaveAUTR")

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustHaveAUTRPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustHaveAUTRPage, value))
            is5mldEnabled <- featureFlagService.is5mldEnabled()
            _ <- registrationsRepository.set(updatedAnswers)
          } yield {
            Redirect(navigator.nextPage(TrustHaveAUTRPage, mode, draftId, request.affinityGroup, is5mldEnabled)(updatedAnswers))
          }
        }
      )
  }
}
