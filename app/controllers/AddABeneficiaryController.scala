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
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
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
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       formProvider: AddABeneficiaryFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AddABeneficiaryView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  private def routes(draftId: String) =
    identify andThen getData(draftId) andThen requireData

  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addABeneficiary.heading")
      case 1 => Messages("addABeneficiary.singular.heading")
      case size => Messages("addABeneficiary.count.heading", size)
    }
  }

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = routes(draftId) {
    implicit request =>

      val beneficiaries = new AddABeneficiaryViewHelper(request.userAnswers, draftId).rows

      val count = beneficiaries.count

      Ok(view(form, mode, draftId, beneficiaries.inProgress, beneficiaries.complete, heading(count)))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = routes(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val beneficiaries = new AddABeneficiaryViewHelper(request.userAnswers, draftId).rows

          val count = beneficiaries.count

          Future.successful(BadRequest(view(formWithErrors, mode, draftId, beneficiaries.inProgress, beneficiaries.complete, heading(count))))

        },

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddABeneficiaryPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddABeneficiaryPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
