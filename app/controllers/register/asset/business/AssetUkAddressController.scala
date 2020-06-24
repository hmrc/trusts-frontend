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
import forms.UKAddressFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.asset.business.{AssetNamePage, AssetUkAddressPage}
import pages.register.trustees.organisation.{TrusteeOrgAddressUkPage, TrusteeOrgNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Assets
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.asset.buisness.AssetUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class AssetUkAddressController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             registrationsRepository: RegistrationsRepository,
                                             navigator: Navigator,
                                             validateIndex: IndexActionFilterProvider,
                                             identify: RegistrationIdentifierAction,
                                             getData: DraftIdRetrievalActionProvider,
                                             requireData: RegistrationDataRequiredAction,
                                             requiredAnswer: RequiredAnswerActionProvider,
                                             formProvider: UKAddressFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: AssetUkAddressView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, Assets) andThen
      requiredAnswer(RequiredAnswer(AssetNamePage(index), routes.AssetNameController.onPageLoad(NormalMode, index, draftId)))

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val businessName = request.userAnswers.get(AssetNamePage(index)).get

      val preparedForm = request.userAnswers.get(AssetUkAddressPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index, businessName))
  }


  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val businessName = request.userAnswers.get(AssetNamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index, businessName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AssetUkAddressPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AssetUkAddressPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
