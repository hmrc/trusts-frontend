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

package controllers.trustees

import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import forms.UKAddressFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.trustees.{IsThisLeadTrusteePage, TrusteesNamePage, TrusteesUkAddressPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.trustees.TrusteesUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class TrusteesUkAddressController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             registrationsRepository: RegistrationsRepository,
                                             navigator: Navigator,
                                             validateIndex: IndexActionFilterProvider,
                                             identify: IdentifierAction,
                                             getData: DraftIdRetrievalActionProvider,
                                             requireData: DataRequiredAction,
                                             requiredAnswer: RequiredAnswerActionProvider,
                                             formProvider: UKAddressFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: TrusteesUkAddressView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, Trustees) andThen
      requiredAnswer(RequiredAnswer(TrusteesNamePage(index), routes.TrusteesNameController.onPageLoad(NormalMode, index, draftId)))

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val messagePrefix: String = getMessagePrefix(index, request)

      val trusteeName = request.userAnswers.get(TrusteesNamePage(index)).get.toString

      val preparedForm = request.userAnswers.get(TrusteesUkAddressPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index, messagePrefix, trusteeName))
  }

  private def getMessagePrefix(index: Int, request: DataRequest[AnyContent]) = {
    val isLead = request.userAnswers.get(IsThisLeadTrusteePage(index)).get

    val messagePrefix = if (isLead) {
      "leadTrusteeUkAddress"
    } else {
      "trusteeUkAddress"
    }
    messagePrefix
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val messagePrefix: String = getMessagePrefix(index, request)

      val trusteeName = request.userAnswers.get(TrusteesNamePage(index)).get.toString

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index, messagePrefix, trusteeName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteesUkAddressPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrusteesUkAddressPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
