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
import forms.TrusteesDateOfBirthFormProvider
import javax.inject.Inject
import models.{FullName, Mode}
import models.entities.Trustee
import navigation.Navigator
import pages.{Trustees, TrusteesDateOfBirthPage, TrusteesNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.TrusteesDateOfBirthView

import scala.concurrent.{ExecutionContext, Future}

class TrusteesDateOfBirthController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  sessionRepository: SessionRepository,
                                                  navigator: Navigator,
                                                  identify: IdentifierAction,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  validateIndex : IndexActionFilterProvider,
                                                  requiredAnswer: RequiredAnswerActionProvider,
                                                  formProvider: TrusteesDateOfBirthFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: TrusteesDateOfBirthView
                                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def routes(index : Int) =
      identify andThen
      getData andThen
      requireData andThen
      validateIndex(index, Trustees) andThen
      requiredAnswer(RequiredAnswer(TrusteesNamePage(index)))

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = routes(index) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrusteesDateOfBirthPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val trusteeName = request.userAnswers.get(TrusteesNamePage(index)).get.toString

      Ok(view(preparedForm, mode, index, trusteeName))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = routes(index).async {
    implicit request =>

      val trusteeName = request.userAnswers.get(TrusteesNamePage(index)).get.toString

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, index, trusteeName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteesDateOfBirthPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrusteesDateOfBirthPage(index), mode)(updatedAnswers))
        }
      )
  }
}
