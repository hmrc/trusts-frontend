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
import forms.trustees.{AddATrusteeFormProvider, AddATrusteeYesNoFormProvider}
import javax.inject.Inject
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.trustees.{AddATrusteePage, AddATrusteeYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddATrusteeViewHelper
import views.html.trustees.{AddATrusteeView, AddATrusteeYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddATrusteeController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       addAnotherFormProvider: AddATrusteeFormProvider,
                                       yesNoFormProvider: AddATrusteeYesNoFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       addAnotherView: AddATrusteeView,
                                       yesNoView: AddATrusteeYesNoView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val addAnotherForm = addAnotherFormProvider()
  val yesNoForm: Form[Boolean] = yesNoFormProvider()

  private def actions(draftId: String) =
    identify andThen getData(draftId) andThen requireData

  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addATrustee.heading")
      case 1 => Messages("addATrustee.singular.heading")
      case size => Messages("addATrustee.count.heading", size)
    }
  }

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val trustees = new AddATrusteeViewHelper(request.userAnswers, draftId).rows

      val isLeadTrusteeDefined = request.userAnswers.get(Trustees).toList.flatten.exists(trustee => trustee.isLead)

      trustees.count match {
        case 0 =>
          Ok(yesNoView(yesNoForm, mode, draftId))
        case count =>
          Ok(addAnotherView(addAnotherForm, mode, draftId, trustees.inProgress, trustees.complete, isLeadTrusteeDefined, heading(count)))
      }
  }

  def submitOne(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors, mode, draftId)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteeYesNoPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddATrusteeYesNoPage, mode, draftId)(updatedAnswers))
        }
      )
  }

  def submitAnother(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val trustees = new AddATrusteeViewHelper(request.userAnswers, draftId).rows

          val isLeadTrusteeDefined = request.userAnswers.get(Trustees).toList.flatten.exists(trustee => trustee.isLead)

          Future.successful(BadRequest(
            addAnotherView(
              formWithErrors,
              mode,
              draftId,
              trustees.inProgress,
              trustees.complete,
              isLeadTrusteeDefined,
              heading(trustees.count)
            )
          ))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddATrusteePage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
