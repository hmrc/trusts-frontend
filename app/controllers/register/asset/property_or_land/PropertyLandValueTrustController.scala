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

package controllers.register.asset.property_or_land

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import forms.property_or_land.PropertyLandValueTrustFormProvider
import javax.inject.Inject
import models.Mode
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.asset.property_or_land.{PropertyLandValueTrustPage, PropertyOrLandTotalValuePage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.annotations.PropertyOrLand
import views.html.register.asset.property_or_land.PropertyLandValueTrustView

import scala.concurrent.{ExecutionContext, Future}

class PropertyLandValueTrustController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  registrationsRepository: RegistrationsRepository,
                                                  @PropertyOrLand navigator: Navigator,
                                                  identify: RegistrationIdentifierAction,
                                                  getData: DraftIdRetrievalActionProvider,
                                                  requireData: RegistrationDataRequiredAction,
                                                  validateIndex: IndexActionFilterProvider,
                                                  formProvider: PropertyLandValueTrustFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: PropertyLandValueTrustView
                                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, sections.Assets)

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      totalValue(mode, index, draftId) match {
        case Left(value) =>
          val form = formProvider.withMaxValue(value)

          val preparedForm = request.userAnswers.get(PropertyLandValueTrustPage(index)) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mode, index, draftId))
        case Right(redirect) =>
          redirect
      }
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      totalValue(mode, index, draftId) match {
        case Left(value) =>
          val form = formProvider.withMaxValue(value)

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, index, draftId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(PropertyLandValueTrustPage(index), value))
                _              <- registrationsRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(PropertyLandValueTrustPage(index), mode, draftId)(updatedAnswers))
            }
          )
        case Right(redirect) =>
          Future.successful(redirect)
      }
  }

  private def totalValue(mode: Mode, index: Int, draftId: String)(implicit request: RegistrationDataRequest[AnyContent]): Either[Long, Result] = {
    request.userAnswers.get(PropertyOrLandTotalValuePage(index)) match {
      case Some(value) =>
        Left(value)
      case _ =>
        logger.info(s"[totalValue][Session ID: ${request.sessionId}] Total value not found. Redirecting to total value page.")
        Right(Redirect(routes.PropertyOrLandTotalValueController.onPageLoad(mode, index, draftId)))
    }
  }
}
