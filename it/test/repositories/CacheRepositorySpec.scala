/*
 * Copyright 2026 HM Revenue & Customs
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
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import repositories.CacheRepositoryImpl
import uk.gov.hmrc.mongo.test.MongoSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CacheRepositorySpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MongoSupport
    with MongoSuite
    with BeforeAndAfterEach {

  override def beforeEach(): Unit =
    Await.result(repository.collection.deleteMany(BsonDocument()).toFuture(), Duration.Inf)

  lazy val repository: CacheRepositoryImpl = new CacheRepositoryImpl(mongoComponent, config)

  "a cache repository" should {

    "must return true when creating document for given internal id" in {

      val internalId = "internalId1"

      val userAnswers = MatchingAndSuitabilityUserAnswers(internalId)

      val initial = repository.set(userAnswers).futureValue

      initial shouldBe true
    }

    "must return true when updating document for given internal id" in {

      val internalId = "internalId2"

      val userAnswers = MatchingAndSuitabilityUserAnswers(internalId)

      repository.set(userAnswers).futureValue

      val updated = repository.set(userAnswers).futureValue

      updated shouldBe true
    }

    "must return None when no cache exists" in {

      val internalId = "internalId3"

      repository.get(internalId).futureValue shouldBe None
    }

    "must return Some user answers when document exists for given internal id" in {

      val internalId = "internalId4"

      val userAnswers = MatchingAndSuitabilityUserAnswers(internalId)

      val initial = repository.set(userAnswers).futureValue

      initial shouldBe true

      repository.get(internalId).futureValue.value.internalId shouldBe userAnswers.internalId
      repository.get(internalId).futureValue.value.data       shouldBe userAnswers.data
    }
  }

}
