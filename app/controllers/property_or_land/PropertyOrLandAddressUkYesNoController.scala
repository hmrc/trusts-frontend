/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers.property_or_land

import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.property_or_land.PropertyOrLandAddressUkYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.annotations.PropertyOrLand
import views.html.property_or_land.PropertyOrLandAddressUkYesNoView

import scala.concurrent.{ExecutionContext, Future}

class PropertyOrLandAddressUkYesNoController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        registrationsRepository: RegistrationsRepository,
                                                        @PropertyOrLand navigator: Navigator,
                                                        identify: IdentifierAction,
                                                        getData: DraftIdRetrievalActionProvider,
                                                        requireData: DataRequiredAction,
                                                        validateIndex: IndexActionFilterProvider,
                                                        yesNoFormProvider: YesNoFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: PropertyOrLandAddressUkYesNoView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("propertyOrLandAddressUkYesNo")

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, sections.Assets)

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(PropertyOrLandAddressUkYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index))
  }

  def onSubmit(mode: Mode, index: Int, draftId : String) = actions(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PropertyOrLandAddressUkYesNoPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PropertyOrLandAddressUkYesNoPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
