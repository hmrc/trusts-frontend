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

package controllers.register.settlors.living_settlor

import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import forms.PassportOrIdCardFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.settlors.living_settlor.{SettlorIndividualNamePage, SettlorIndividualPassportPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.LivingSettlors
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.annotations.LivingSettlor
import utils.countryOptions.CountryOptions
import views.html.register.settlors.living_settlor.SettlorIndividualPassportView

import scala.concurrent.{ExecutionContext, Future}

class SettlorIndividualPassportController @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     registrationsRepository: RegistrationsRepository,
                                                     @LivingSettlor navigator: Navigator,
                                                     identify: IdentifierAction,
                                                     getData: DraftIdRetrievalActionProvider,
                                                     validateIndex: IndexActionFilterProvider,
                                                     requireData: DataRequiredAction,
                                                     requiredAnswer: RequiredAnswerActionProvider,
                                                     formProvider: PassportOrIdCardFormProvider,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: SettlorIndividualPassportView,
                                                     val countryOptions: CountryOptions
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider("settlorIndividualPassport")

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, LivingSettlors) andThen
      requiredAnswer(RequiredAnswer(SettlorIndividualNamePage(index), routes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId)))


  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val name = request.userAnswers.get(SettlorIndividualNamePage(index)).get

      val preparedForm = request.userAnswers.get(SettlorIndividualPassportPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, mode, draftId, index, name))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorIndividualNamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, mode, draftId, index, name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorIndividualPassportPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorIndividualPassportPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}