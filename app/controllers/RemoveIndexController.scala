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

import models.requests.DataRequest
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Call, Result}
import play.twirl.api.HtmlFormat
import queries.Settable
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

trait RemoveIndexController extends FrontendBaseController with I18nSupport {

  val messagesPrefix : String

  val form: Form[Boolean]

  def sessionRepository : SessionRepository

  def redirect(draftId : String) : Call

  def formRoute(draftId : String, index: Int) : Call

  def removeQuery(index : Int) : Settable[_]

  def content(index: Int)(implicit request: DataRequest[AnyContent]) : String

  def view(form: Form[_], index : Int, draftId : String)
          (implicit request: DataRequest[AnyContent], messagesApi: MessagesApi) : HtmlFormat.Appendable

  def get(index : Int, draftId: String)(implicit request: DataRequest[AnyContent]) : Result =
    Ok(view(form, index, draftId))

  def remove(index : Int, draftId : String)
            (implicit request : DataRequest[AnyContent], ec: ExecutionContext) = {
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, index, draftId))),

        value => {
          if (value) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.remove(removeQuery(index)))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(redirect(draftId).url)
          } else {
            Future.successful(Redirect(redirect(draftId).url))
          }
        }
      )
  }

}
