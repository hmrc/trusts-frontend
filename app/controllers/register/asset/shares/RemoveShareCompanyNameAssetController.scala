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

package controllers.register.asset.shares

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.register.asset.RemoveAssetController
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import pages.QuestionPage
import pages.register.asset.shares.ShareCompanyNamePage
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents}
import repositories.RegistrationsRepository
import views.html.RemoveIndexView

class RemoveShareCompanyNameAssetController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       override val registrationsRepository: RegistrationsRepository,
                                                       override val formProvider: RemoveIndexFormProvider,
                                                       identify: RegistrationIdentifierAction,
                                                       getData: DraftIdRetrievalActionProvider,
                                                       requireData: RegistrationDataRequiredAction,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       require: RequiredAnswerActionProvider,
                                                       val removeView: RemoveIndexView
                                           ) extends RemoveAssetController {

  override def page(index: Int): QuestionPage[String] = ShareCompanyNamePage(index)

  override val messagesPrefix : String = "removeShareAsset"

  override def actions(draftId : String, index: Int) =
    identify andThen getData(draftId) andThen requireData

  override def content(index: Int)(implicit request: RegistrationDataRequest[AnyContent]) : String =
    request.userAnswers.get(page(index)).getOrElse(Messages(s"$messagesPrefix.default"))

  override def formRoute(draftId: String, index: Int): Call =
    controllers.register.asset.shares.routes.RemoveShareCompanyNameAssetController.onSubmit(index, draftId)
}


