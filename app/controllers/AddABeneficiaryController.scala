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
import forms.AddABeneficiaryFormProvider
import javax.inject.Inject
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.AddABeneficiaryPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddABeneficiaryViewHelper
import views.html.AddABeneficiaryView

import scala.concurrent.{ExecutionContext, Future}

class AddABeneficiaryController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: AddABeneficiaryFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AddABeneficiaryView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  private def routes =
    identify andThen getData andThen requireData

  def onPageLoad(mode: Mode): Action[AnyContent] = routes {
    implicit request =>

      val beneficiaries = new AddABeneficiaryViewHelper(request.userAnswers).rows

      Ok(view(form, mode, beneficiaries.inProgress, beneficiaries.complete))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = routes.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val beneficiaries = new AddABeneficiaryViewHelper(request.userAnswers).rows

          Future.successful(BadRequest(view(formWithErrors, mode, beneficiaries.inProgress, beneficiaries.complete)))

        },

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddABeneficiaryPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddABeneficiaryPage, mode)(updatedAnswers))
        }
      )
  }
}
