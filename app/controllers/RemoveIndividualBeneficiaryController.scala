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
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.IndividualBeneficiaryNamePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import queries.{RemoveAssetQuery, RemoveIndividualBeneficiaryQuery, Settable}
import repositories.SessionRepository
import views.html.RemoveIndividualBeneficiaryView

import scala.concurrent.ExecutionContext

class RemoveIndividualBeneficiaryController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val sessionRepository: SessionRepository,
                                         identify: IdentifierAction,
                                         getData: DraftIdRetrievalActionProvider,
                                         requireData: DataRequiredAction,
                                         formProvider: RemoveIndexFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         removeView: RemoveIndividualBeneficiaryView,
                                         require: RequiredAnswerActionProvider
                                 )(implicit ec: ExecutionContext) extends RemoveIndexController {

  def actions(draftId : String, index: Int) =
    identify andThen getData(draftId) andThen
      requireData andThen
      require(RequiredAnswer(IndividualBeneficiaryNamePage(index), redirect(draftId)))

  override def redirect(draftId : String) : Call = routes.AddABeneficiaryController.onPageLoad(draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveIndividualBeneficiaryQuery(index)

  override val form: Form[Boolean] = formProvider.apply("removeIndividualBeneficiary")

  override def content(index: Int)(implicit request: DataRequest[AnyContent]) : String =
    request.userAnswers.get(IndividualBeneficiaryNamePage(index)).get.toString

  override def view(form: Form[_], mode: Mode, index: Int, draftId: String)
                   (implicit request: DataRequest[AnyContent], messagesApi: MessagesApi): HtmlFormat.Appendable = {
    removeView(form, mode, index, draftId, content(index))
  }

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(draftId, index) {
    implicit request =>
      get(mode, index, draftId)
  }

  def onSubmit(mode: Mode, index: Int, draftId : String) = actions(draftId, index).async {
    implicit request =>
      remove(mode, index, draftId)
  }
}
