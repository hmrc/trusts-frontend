/*
 * Copyright 2018 HM Revenue & Customs
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

package pages

import org.scalactic.Uniformity
import play.api.libs.json.{Format, Json}
import utils.UserAnswers

trait UserAnswersUniformities {

  def setTo[A](page: QuestionPage[A], value: A)(implicit format: Format[A]) = new Uniformity[UserAnswers] {
    override def normalizedOrSame(b: Any): Any =
      b match {
        case ua: UserAnswers => normalized(ua)
        case _               => b
      }

    override def normalizedCanHandle(b: Any): Boolean =
      b.isInstanceOf[UserAnswers]

    override def normalized(a: UserAnswers): UserAnswers =
      UserAnswers(a.cacheMap copy (data = a.cacheMap.data + (page.toString -> Json.toJson(value))))
  }

  def strippedOf(page: Page) = new Uniformity[UserAnswers] {
    override def normalizedOrSame(b: Any): Any =
      b match {
        case ua: UserAnswers => normalized(ua)
        case _                => b
      }

    override def normalizedCanHandle(b: Any): Boolean =
      b.isInstanceOf[UserAnswers]

    override def normalized(a: UserAnswers): UserAnswers =
      UserAnswers(a.cacheMap copy (data = a.cacheMap.data - page))
  }
}
