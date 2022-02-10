/*
 * Copyright 2022 HM Revenue & Customs
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

import models.core.MatchingAndSuitabilityUserAnswers
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

class CacheRepositorySpec extends AsyncFreeSpec with MustMatchers
  with ScalaFutures with OptionValues with MongoSuite {

  "a cache repository" - {

    "must return true when creating document for given internal id" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "internalId1"

        val repository = app.injector.instanceOf[CacheRepository]

        val userAnswers = MatchingAndSuitabilityUserAnswers(internalId)

        val initial = repository.set(userAnswers).futureValue

        initial mustBe true
    }

    "must return true when updating document for given internal id" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "internalId2"

        val repository = app.injector.instanceOf[CacheRepository]

        val userAnswers = MatchingAndSuitabilityUserAnswers(internalId)

        repository.set(userAnswers).futureValue

        val updated = repository.set(userAnswers).futureValue

        updated mustBe true
    }

    "must return None when no cache exists" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "internalId3"

        val repository = app.injector.instanceOf[CacheRepository]
        repository.get(internalId).futureValue mustBe None
    }

    "must return Some user answers when document exists for given internal id" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "internalId4"

        val repository = app.injector.instanceOf[CacheRepository]

        val userAnswers = MatchingAndSuitabilityUserAnswers(internalId)

        val initial = repository.set(userAnswers).futureValue

        initial mustBe true

        repository.get(internalId).futureValue.value.internalId mustBe userAnswers.internalId
        repository.get(internalId).futureValue.value.data mustBe userAnswers.data
    }
  }
}
