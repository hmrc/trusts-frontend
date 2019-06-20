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
import controllers.filters.IndexActionFilterProvider
import forms.IsThisLeadTrusteeFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.IsThisLeadTrusteePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.Trustees
import viewmodels.addAnother.TrusteeViewModel
import views.html.IsThisLeadTrusteeView

import scala.concurrent.{ExecutionContext, Future}

class IsThisLeadTrusteeController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             sessionRepository: SessionRepository,
                                             navigator: Navigator,
                                             identify: IdentifierAction,
                                             getData: DraftIdRetrievalActionProvider,
                                             requireData: DataRequiredAction,
                                             validateIndex : IndexActionFilterProvider,
                                             formProvider: IsThisLeadTrusteeFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: IsThisLeadTrusteeView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def actions(index : Int, draftId: String) = identify andThen getData(draftId) andThen requireData andThen validateIndex(index, Trustees)

  def onPageLoad(mode: Mode, index : Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      def renderView = {
        val preparedForm = request.userAnswers.get(IsThisLeadTrusteePage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, mode, draftId, index)))
      }

      def leadTrustee : Option[(TrusteeViewModel, Int)] = {
        val trustees = request.userAnswers.get(Trustees).getOrElse(Nil).zipWithIndex

        trustees.find{ case (trustee, _) => trustee.isLead}
      }

      leadTrustee match {
        case Some((_, i)) =>
          def currentIndexIsNotTheLeadTrustee = i != index

          // A lead trustee has already been added, if the current index is not the lead trustee
          // answer the question on behalf of the user and redirect to next page
          if (currentIndexIsNotTheLeadTrustee) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(IsThisLeadTrusteePage(index), false))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(IsThisLeadTrusteePage(index), mode, draftId)(updatedAnswers))
          } else {
            renderView
          }
        case None =>
          renderView
      }
  }


  def onSubmit(mode: Mode, index : Int, draftId: String) = actions(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IsThisLeadTrusteePage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IsThisLeadTrusteePage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
