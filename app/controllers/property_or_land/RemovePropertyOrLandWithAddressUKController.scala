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

package controllers.property_or_land

import controllers.RemoveAssetController
import controllers.actions._
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.UKAddress
import models.requests.DataRequest
import pages.QuestionPage
import pages.property_or_land.PropertyOrLandUKAddressPage
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.RemoveIndexView

class RemovePropertyOrLandWithAddressUKController @Inject()(
                                                               override val messagesApi: MessagesApi,
                                                               override val sessionRepository: SessionRepository,
                                                               override val formProvider: RemoveIndexFormProvider,
                                                               identify: IdentifierAction,
                                                               getData: DraftIdRetrievalActionProvider,
                                                               requireData: DataRequiredAction,
                                                               val controllerComponents: MessagesControllerComponents,
                                                               require: RequiredAnswerActionProvider,
                                                               val removeView: RemoveIndexView
                                                             ) extends RemoveAssetController {

  override val messagesPrefix: String = "removePropertyOrLandAsset"

  override def actions(draftId: String, index: Int) =
    identify andThen getData(draftId) andThen
      requireData andThen
      require(RequiredAnswer(page(index), redirect(draftId)))

  override def page(index: Int): QuestionPage[UKAddress] = PropertyOrLandUKAddressPage(index)

  override def content(index: Int)(implicit request: DataRequest[AnyContent]): String =
    request.userAnswers.get(page(index)).get.line1

  override def formRoute(draftId: String, index: Int): Call =
    controllers.property_or_land.routes.RemovePropertyOrLandWithAddressUKController.onSubmit(index, draftId)
}


