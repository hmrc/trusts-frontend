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

package controllers.register.asset.other

import controllers.actions.register.asset.RequireOtherAssetDescriptionAction
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.ValueFormProvider
import javax.inject.Inject
import models.Mode
import models.requests.asset.OtherAssetDescriptionRequest
import navigation.Navigator
import pages.register.asset.other.OtherAssetValuePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.asset.other.OtherAssetValueView

import scala.concurrent.{ExecutionContext, Future}

class OtherAssetValueController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           registrationsRepository: RegistrationsRepository,
                                           navigator: Navigator,
                                           identify: RegistrationIdentifierAction,
                                           getData: DraftIdRetrievalActionProvider,
                                           requireData: RegistrationDataRequiredAction,
                                           formProvider: ValueFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: OtherAssetValueView
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(mode: Mode, index: Int, draftId: String): ActionBuilder[OtherAssetDescriptionRequest, AnyContent] =
    identify andThen getData(draftId) andThen requireData andThen new RequireOtherAssetDescriptionAction(mode, index, draftId)

  val form: Form[String] = formProvider.withPrefix(prefix = "assets.other.value")

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(mode, index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(OtherAssetValuePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index, request.description))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(mode, index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index, request.description))),

        value => {

          val answers = request.userAnswers.set(OtherAssetValuePage(index), value)

          for {
            updatedAnswers <- Future.fromTry(answers)
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(OtherAssetValuePage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
