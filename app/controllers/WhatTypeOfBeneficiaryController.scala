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
import forms.WhatTypeOfBeneficiaryFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.WhatTypeOfBeneficiaryPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.{ClassOfBeneficiaries, IndividualBeneficiaries}
import views.html.WhatTypeOfBeneficiaryView

import scala.concurrent.{ExecutionContext, Future}

class WhatTypeOfBeneficiaryController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       formProvider: WhatTypeOfBeneficiaryFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhatTypeOfBeneficiaryView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  private def actions(draftId: String) = identify andThen getData(draftId) andThen requireData

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>
      Ok(view(form, mode,isAnyBeneficiaryAdded(request)))
  }



  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode,isAnyBeneficiaryAdded(request)))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatTypeOfBeneficiaryPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatTypeOfBeneficiaryPage, mode, draftId)(updatedAnswers))
        }
      )
  }

  private def isAnyBeneficiaryAdded(request: DataRequest[AnyContent]) = {
   request.userAnswers.get(IndividualBeneficiaries).
     getOrElse(List.empty).nonEmpty  ||
     request.userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty).nonEmpty
  }
}
