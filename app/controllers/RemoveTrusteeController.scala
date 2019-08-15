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
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import pages.TrusteesNamePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import queries.{RemoveTrusteeQuery, Settable}
import repositories.SessionRepository
import views.html.RemoveIndexView

import scala.concurrent.ExecutionContext

class RemoveTrusteeController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val sessionRepository: SessionRepository,
                                              identify: IdentifierAction,
                                              getData: DraftIdRetrievalActionProvider,
                                              requireData: DataRequiredAction,
                                              formProvider: RemoveIndexFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              removeView: RemoveIndexView,
                                              require: RequiredAnswerActionProvider
                                 )(implicit ec: ExecutionContext) extends RemoveIndexController with I18nSupport {

  override val messagesPrefix : String = "removeATrustee"

  override val form: Form[Boolean] = formProvider.apply(messagesPrefix)

  def actions(draftId : String, index: Int) =
    identify andThen getData(draftId) andThen
      requireData andThen
      require(RequiredAnswer(TrusteesNamePage(index), redirect(draftId)))

  override def redirect(draftId : String) : Call =
    routes.AddATrusteeController.onPageLoad(draftId)

  override def formRoute(draftId: String, index: Int): Call =
    routes.RemoveTrusteeController.onSubmit(index, draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveTrusteeQuery(index)

  override def content(index: Int)(implicit request: DataRequest[AnyContent]) : String =
    request.userAnswers.get(TrusteesNamePage(index)).get.toString

  override def view(form: Form[_], index: Int, draftId: String)
                   (implicit request: DataRequest[AnyContent], messagesApi: MessagesApi): HtmlFormat.Appendable = {
    removeView(messagesPrefix, form, index, draftId, content(index), formRoute(draftId, index))
  }

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(draftId, index) {
    implicit request =>
      get(index, draftId)
  }

  def onSubmit(index: Int, draftId : String) = actions(draftId, index).async {
    implicit request =>
      remove(index, draftId)
  }
}
