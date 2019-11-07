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

package controllers.trustees

import controllers.RemoveIndexController
import controllers.actions._
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.FullName
import models.requests.DataRequest
import pages.QuestionPage
import pages.trustees.TrusteesNamePage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents}
import queries.{RemoveTrusteeQuery, Settable}
import repositories.RegistrationsRepository
import views.html.RemoveIndexView

import scala.concurrent.ExecutionContext

class RemoveTrusteeController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val registrationsRepository: RegistrationsRepository,
                                         identify: IdentifierAction,
                                         getData: DraftIdRetrievalActionProvider,
                                         requireData: DataRequiredAction,
                                         val formProvider: RemoveIndexFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         val removeView: RemoveIndexView,
                                         require: RequiredAnswerActionProvider
                                 )(implicit ec: ExecutionContext) extends RemoveIndexController with I18nSupport {

  override val messagesPrefix : String = "removeATrustee"

  override def page(index: Int) : QuestionPage[FullName] = TrusteesNamePage(index)

  override def actions(draftId : String, index: Int) =
    identify andThen getData(draftId) andThen requireData

  override def redirect(draftId : String) : Call =
    routes.AddATrusteeController.onPageLoad(draftId)

  override def formRoute(draftId: String, index: Int): Call =
    routes.RemoveTrusteeController.onSubmit(index, draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveTrusteeQuery(index)

  override def content(index: Int)(implicit request: DataRequest[AnyContent]) : String =
    request.userAnswers.get(page(index)).map(_.toString).getOrElse(Messages(s"$messagesPrefix.default"))

}
