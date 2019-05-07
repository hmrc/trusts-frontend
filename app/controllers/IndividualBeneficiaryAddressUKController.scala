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

package controllers

import controllers.actions._
import forms.{ UKAddressFormProvider}
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.{IndividualBeneficiaryAddressUKPage, IndividualBeneficiaryNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.IndividualBeneficiaryAddressUKView

import scala.concurrent.{ExecutionContext, Future}

class IndividualBeneficiaryAddressUKController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionRepository: SessionRepository,
                                      navigator: Navigator,
                                      identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      requiredAnswer: RequiredAnswerActionProvider,
                                      formProvider: UKAddressFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: IndividualBeneficiaryAddressUKView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def actions(index: Int) =
    identify andThen
      getData andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(IndividualBeneficiaryNamePage(index), routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode,index)))

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = actions(index) {
    implicit request =>

      val name = request.userAnswers.get(IndividualBeneficiaryNamePage(index)).get

      val preparedForm = request.userAnswers.get(IndividualBeneficiaryAddressUKPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, name, index))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = actions(index).async {
    implicit request =>

      val name = request.userAnswers.get(IndividualBeneficiaryNamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, name, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualBeneficiaryAddressUKPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IndividualBeneficiaryAddressUKPage(index), mode)(updatedAnswers))
        }
      )
  }
}
