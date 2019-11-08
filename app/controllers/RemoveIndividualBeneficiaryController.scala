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
import models.{FullName, Mode, NormalMode}
import models.requests.DataRequest
import pages.{IndividualBeneficiaryNamePage, QuestionPage}
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import queries.{RemoveIndividualBeneficiaryQuery, Settable}
import repositories.RegistrationsRepository
import views.html.RemoveIndexView

import scala.concurrent.ExecutionContext

class RemoveIndividualBeneficiaryController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       override val registrationsRepository: RegistrationsRepository,
                                                       identify: IdentifierAction,
                                                       getData: DraftIdRetrievalActionProvider,
                                                       requireData: DataRequiredAction,
                                                       val formProvider: RemoveIndexFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       val removeView: RemoveIndexView,
                                                       require: RequiredAnswerActionProvider
                                 )(implicit ec: ExecutionContext) extends RemoveIndexController {

  override val messagesPrefix : String = "removeIndividualBeneficiary"

  override def page(index: Int): QuestionPage[FullName] = IndividualBeneficiaryNamePage(index)

  override def actions(draftId : String, index: Int) =
    identify andThen getData(draftId) andThen requireData

  override def redirect(draftId : String) : Call =
    routes.AddABeneficiaryController.onPageLoad(draftId)

  override def formRoute(draftId: String, index: Int): Call =
    routes.RemoveIndividualBeneficiaryController.onSubmit(index, draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveIndividualBeneficiaryQuery(index)

  override def content(index: Int)(implicit request: DataRequest[AnyContent]) : String =
    request.userAnswers.get(page(index)).map(_.toString).getOrElse(Messages(s"$messagesPrefix.default"))

}
