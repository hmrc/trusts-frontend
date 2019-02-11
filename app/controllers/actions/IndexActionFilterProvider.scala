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

package controllers.actions

import com.google.inject.Inject
import controllers.actions.Entity.EntityType
import handlers.ErrorHandler
import models.requests.DataRequest
import pages.QuestionPage
import play.api.http.Status
import play.api.libs.json.JsObject
import play.api.mvc.{ActionFilter, Result}

import scala.concurrent.{ExecutionContext, Future}

object Entity {
  type EntityType = QuestionPage[List[JsObject]]
}

class IndexActionFilter(index : Int,
                        entity : EntityType,
                        protected implicit val executionContext: ExecutionContext,
                        protected val errorHandler : ErrorHandler)
  extends ActionFilter[DataRequest] {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    lazy val numberOfEntities = request.userAnswers.get(entity).getOrElse(List.empty).size

    if (index >= 0 && index <= numberOfEntities) {
      Future.successful(None)
    } else {
      errorHandler.onClientError(request, Status.NOT_FOUND).map(Some(_))
    }

  }

}

class IndexActionFilterProvider @Inject()(executionContext: ExecutionContext,
                                          errorHandler: ErrorHandler) {

  def apply(index: Int, entity : EntityType): IndexActionFilter =
    new IndexActionFilter(index, entity, executionContext, errorHandler)

}