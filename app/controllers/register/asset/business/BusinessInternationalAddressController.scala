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

package controllers.register.asset.business

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import forms.InternationalAddressFormProvider
import javax.inject.Inject
import models.core.pages.InternationalAddress
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.asset.business.{BusinessInternationalAddressPage, BusinessNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Assets
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.asset.buisness.BusinessInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class BusinessInternationalAddressController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        registrationsRepository: RegistrationsRepository,
                                                        navigator: Navigator,
                                                        formProvider: InternationalAddressFormProvider,
                                                        actionSet: StandardActionSets,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: BusinessInternationalAddressView,
                                                        val countryOptions: CountryOptionsNonUK
                                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[InternationalAddress] = formProvider()

  private def actions(index: Int, draftId: String) =
    actionSet.identifiedUserWithDataAnswerAndIndex(
      draftId, RequiredAnswer(BusinessNamePage(index), routes.BusinessNameController.onPageLoad(NormalMode, index, draftId)),index, Assets)

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val businessName = request.userAnswers.get(BusinessNamePage(index)).get

      val preparedForm = request.userAnswers.get(BusinessInternationalAddressPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, mode, index, draftId, businessName))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val businessName = request.userAnswers.get(BusinessNamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, mode, index, draftId, businessName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(BusinessInternationalAddressPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(BusinessInternationalAddressPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
