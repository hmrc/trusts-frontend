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
import forms.TrusteesNameFormProvider
import javax.inject.Inject
import models.Mode
import models.{Mode, NormalMode}
import models.entities.Trustee
import navigation.Navigator
import pages.{IsThisLeadTrusteePage, Trustees, TrusteesNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.TrusteesNameView

import scala.concurrent.{ExecutionContext, Future}

class TrusteesNameController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        validateIndex: IndexActionFilterProvider,
                                        formProvider: TrusteesNameFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: TrusteesNameView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def actions(index: Int) =
    identify andThen getData andThen requireData andThen validateIndex(index, Trustees)

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = actions(index) {
    implicit request =>


      val isLead = request.userAnswers.get(IsThisLeadTrusteePage(index))
      if (isLead.isEmpty) {
       Redirect(routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index))
      } else {

        val heading = if (isLead.contains(true)) {
          Messages("leadTrusteesName.heading")
        } else {
          Messages("trusteesName.heading")
        }

        val preparedForm = request.userAnswers.get(TrusteesNamePage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }


        Ok(view(preparedForm, mode, index, heading))
      }
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = actions(index).async {
    implicit request =>

      val isLead = request.userAnswers.get(IsThisLeadTrusteePage(index))

      if (isLead.isEmpty) {
        Future.successful(Redirect(routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index)))
      } else {

        val heading = if (isLead.contains(true)) {
          Messages("leadTrusteesName.heading")
        } else {
          Messages("trusteesName.heading")
        }

        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, mode, index, heading))),

          value => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteesNamePage(index), value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(TrusteesNamePage(index), mode)(updatedAnswers))
          }
        )

      }

  }
}
