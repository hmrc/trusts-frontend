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
import forms.WhatIsTheUTRFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.WhatIsTheUTRPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.WhatIsTheUTRView

import scala.concurrent.{ExecutionContext, Future}

class WhatIsTheUTRController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        registrationsRepository: RegistrationsRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: DataRequiredAction,
                                        formProvider: WhatIsTheUTRFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhatIsTheUTRView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) = identify andThen getData(draftId) andThen requireData

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatIsTheUTRPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, routes.WhatIsTheUTRController.onSubmit(mode, draftId)))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, routes.WhatIsTheUTRController.onSubmit(mode, draftId)))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsTheUTRPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatIsTheUTRPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
