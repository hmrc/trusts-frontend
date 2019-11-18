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
import forms.PostcodeForTheTrustFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.PostcodeForTheTrustPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.PostcodeForTheTrustView

import scala.concurrent.{ExecutionContext, Future}

class PostcodeForTheTrustController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               registrationsRepository: RegistrationsRepository,
                                               navigator: Navigator,
                                               identify: IdentifyForRegistration,
                                               getData: DraftIdRetrievalActionProvider,
                                               requireData: DataRequiredAction,
                                               formProvider: PostcodeForTheTrustFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: PostcodeForTheTrustView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) = identify andThen getData(draftId) andThen requireData

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(PostcodeForTheTrustPage) match {
        case None => form
        case v @ Some(_) => form.fill(v)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      def redirect(userAnswers : UserAnswers) = Redirect(navigator.nextPage(PostcodeForTheTrustPage, mode, draftId)(userAnswers))

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),
        value =>
          value match {
          case Some(v) =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PostcodeForTheTrustPage, v))
              _ <- registrationsRepository.set(updatedAnswers)
            } yield redirect(updatedAnswers)
          case None =>
            Future.successful(redirect(request.userAnswers))
        }
      )
  }
}
