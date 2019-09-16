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

package controllers.living_settlor

import controllers.RemoveIndexController
import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.FullName
import models.requests.DataRequest
import navigation.Navigator
import pages.QuestionPage
import pages.living_settlor.SettlorIndividualNamePage
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents}
import queries.{RemoveAssetQuery, RemoveSettlorQuery, Settable}
import repositories.SessionRepository
import sections.LivingSettlors
import views.html.RemoveIndexView

import scala.concurrent.ExecutionContext


class RemoveSettlorController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val sessionRepository: SessionRepository,
                                         navigator: Navigator,
                                         identify: IdentifierAction,
                                         validateIndex : IndexActionFilterProvider,
                                         getData: DraftIdRetrievalActionProvider,
                                         requireData: DataRequiredAction,
                                         override val formProvider: RemoveIndexFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         override val removeView: RemoveIndexView
                                 )(implicit ec: ExecutionContext) extends RemoveIndexController {

  override def page(index: Int): QuestionPage[FullName] = SettlorIndividualNamePage(index)

  override def removeQuery(index: Int): Settable[_] = RemoveSettlorQuery(index)

  override val messagesPrefix : String = "removeSettlor"

  override def actions(draftId : String, index: Int) =
    identify andThen getData(draftId) andThen requireData andThen validateIndex(index, LivingSettlors)

  override def content(index: Int)(implicit request: DataRequest[AnyContent]) : String =
    request.userAnswers.get(page(index)).map(_.toString).getOrElse(Messages(s"$messagesPrefix.default"))

  override def formRoute(draftId: String, index: Int): Call =
    controllers.living_settlor.routes.RemoveSettlorController.onSubmit(index, draftId)

  override def redirect(draftId : String) : Call =
    controllers.routes.AddASettlorController.onPageLoad(draftId)

}