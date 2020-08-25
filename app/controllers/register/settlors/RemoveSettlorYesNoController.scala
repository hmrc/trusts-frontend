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

package controllers.register.settlors

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import forms.YesNoFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsPath, JsValue}
import play.api.mvc._
import repositories.RegistrationsRepository
import sections.settlors.{DeceasedSettlor, LivingSettlors}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.addAnother._
import views.html.register.settlors.RemoveSettlorYesNoView

import scala.concurrent.{ExecutionContext, Future}

class RemoveSettlorYesNoController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              registrationsRepository: RegistrationsRepository,
                                              identify: RegistrationIdentifierAction,
                                              getData: DraftIdRetrievalActionProvider,
                                              requireData: RegistrationDataRequiredAction,
                                              yesNoFormProvider: YesNoFormProvider,
                                              validateIndex: IndexActionFilterProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: RemoveSettlorYesNoView
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("settlors.removeYesNo")

  private val deceasedPath: JsPath = DeceasedSettlor.path

  private def livingPath(index: Int): JsPath = LivingSettlors.path \ index

  private def redirect(draftId: String): Result =
    Redirect(controllers.register.settlors.routes.AddASettlorController.onPageLoad(draftId))

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identify andThen getData(draftId) andThen requireData

  private def actions(index: Int, draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    actions(draftId) andThen validateIndex(index, LivingSettlors)

  def onPageLoadLiving(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      onPageLoad(draftId, routes.RemoveSettlorYesNoController.onSubmitLiving(index, draftId), livingPath(index))
  }

  def onPageLoadDeceased(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      onPageLoad(draftId, routes.RemoveSettlorYesNoController.onSubmitDeceased(draftId), deceasedPath)
  }

  private def onPageLoad(draftId: String, route: Call, path: JsPath)
                        (implicit request: RegistrationDataRequest[AnyContent]): Result = {

    Ok(view(form, draftId, route, label(request.userAnswers.data, path)))
  }

  def onSubmitLiving(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      onSubmit(
        draftId,
        routes.RemoveSettlorYesNoController.onSubmitLiving(index, draftId),
        livingPath(index)
      )
  }

  def onSubmitDeceased(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      onSubmit(
        draftId,
        routes.RemoveSettlorYesNoController.onSubmitDeceased(draftId),
        deceasedPath
      )
  }

  private def onSubmit(draftId: String, route: Call, path: JsPath)
                      (implicit request: RegistrationDataRequest[AnyContent]): Future[Result] = {

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(view(
          formWithErrors,
          draftId,
          route,
          label(request.userAnswers.data, path)
        ))),

      remove => {
        if (remove) {
          for {
            updatedAnswers <- Future.fromTry(
              request.userAnswers.deleteAtPath(path)
            )
            _ <- registrationsRepository.set(updatedAnswers)
          } yield {
            redirect(draftId)
          }
        } else {
          Future.successful(redirect(draftId))
        }
      }
    )
  }

  private def label(json: JsValue, path: JsPath)
                   (implicit request: RegistrationDataRequest[AnyContent]): String = {

    val default: String = request.messages(messagesApi)("settlors.defaultText")

    (for {
      pick <- json.transform(path.json.pick)
      settlor <- pick.validate[SettlorViewModel]
    } yield {
      settlor match {
        case deceased: SettlorDeceasedIndividualViewModel => deceased.name
        case individual: SettlorLivingIndividualViewModel => individual.name
        case business: SettlorBusinessTypeViewModel => business.name
        case _ => default
      }
    }).getOrElse(default)

  }

}
