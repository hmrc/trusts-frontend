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

import controllers.actions.{DataRequiredAction, DraftIdRetrievalActionProvider, IdentifierAction, RequiredAnswerActionProvider}
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import pages.{DefaultRemoveAssetPage, QuestionPage}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents}
import queries.{RemoveAssetQuery, Settable}
import repositories.RegistrationsRepository
import views.html.RemoveIndexView


trait RemoveAssetController extends RemoveIndexController {

  override def redirect(draftId : String) : Call =
    controllers.routes.AddAssetsController.onPageLoad(draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveAssetQuery(index)
}

class DefaultRemoveAssetController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val registrationsRepository: RegistrationsRepository,
                                              override val formProvider: RemoveIndexFormProvider,
                                              identify: IdentifierAction,
                                              getData: DraftIdRetrievalActionProvider,
                                              requireData: DataRequiredAction,
                                              val controllerComponents: MessagesControllerComponents,
                                              require: RequiredAnswerActionProvider,
                                              val removeView: RemoveIndexView
                                            ) extends RemoveAssetController {

  override def page(index: Int): QuestionPage[_] = DefaultRemoveAssetPage

  override val messagesPrefix : String = "removeAsset"

  override def actions(draftId : String, index: Int) =
    identify andThen getData(draftId) andThen requireData

  override def content(index: Int)(implicit request: DataRequest[AnyContent]) : String = Messages(s"$messagesPrefix.default")

  override def formRoute(draftId: String, index: Int): Call =
    controllers.routes.DefaultRemoveAssetController.onSubmit(index, draftId)

}