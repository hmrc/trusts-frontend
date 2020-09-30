/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.actions.register._
import controllers.filters.IndexActionFilterProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import play.api.libs.json.Reads
import play.api.mvc.{ActionBuilder, AnyContent}
import queries.Gettable

import scala.concurrent.ExecutionContext

class StandardActionSets @Inject()(identify: RegistrationIdentifierAction,
                                   getData: DraftIdRetrievalActionProvider,
                                   requireData: RegistrationDataRequiredAction,
                                   requiredAnswerAction: RequiredAnswerActionProvider,
                                   validateIndex: IndexActionFilterProvider) {

  def identifiedUserWithData(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identify andThen getData(draftId) andThen requireData

  def identifiedUserWithRequiredAnswer[T](draftId: String, requiredAnswer: RequiredAnswer[T])
                                      (implicit reads: Reads[T]) :ActionBuilder[RegistrationDataRequest, AnyContent] =
    identifiedUserWithData(draftId) andThen requiredAnswerAction(requiredAnswer)

  def identifiedUserWithDataAnswerAndIndex[T,U](draftId: String, requiredAnswer: RequiredAnswer[T], index: Int, entity : Gettable[List[U]])
                                       (implicit rAReads : Reads[T], eReads : Reads[U],  executionContext: ExecutionContext): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identifiedUserWithRequiredAnswer(draftId, requiredAnswer) andThen validateIndex(index, entity)
}
