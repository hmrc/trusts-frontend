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

package repositories
import models.UserAnswers
import play.api.libs.json.Json

import scala.concurrent.Future

class FakeSessionRepository extends SessionRepository {

  val userAnswersId = "id"

  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  override val started: Future[Unit] = Future.successful(())

  override def get(id: String): Future[Option[UserAnswers]] = Future.successful(Some(emptyUserAnswers))

  override def set(userAnswers: UserAnswers): Future[Boolean] = Future.successful(true)

}
