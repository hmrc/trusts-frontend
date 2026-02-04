/*
 * Copyright 2024 HM Revenue & Customs
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

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import models.core.MatchingAndSuitabilityUserAnswers
import org.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model._
import play.api.libs.json._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CacheRepositoryImpl @Inject() (
  val mongo: MongoComponent,
  val config: FrontendAppConfig
)(implicit val ec: ExecutionContext)
    extends PlayMongoRepository[MatchingAndSuitabilityUserAnswers](
      collectionName = "user-answers",
      mongoComponent = mongo,
      domainFormat = Format(MatchingAndSuitabilityUserAnswers.reads, MatchingAndSuitabilityUserAnswers.writes),
      indexes = Seq(
        IndexModel(
          ascending("updatedAt"),
          IndexOptions()
            .unique(false)
            .name("user-answers-updated-at-index")
            .expireAfter(config.cachettllocalInSeconds, TimeUnit.SECONDS)
        ),
        IndexModel(
          ascending("internalId"),
          IndexOptions()
            .unique(false)
            .name("internal-auth-id-index")
        )
      ),
      replaceIndexes = config.dropIndexes
    )
    with CacheRepository {

  private def selector(internalId: String): Bson = equal("internalId", internalId)

  override def get(internalId: String): Future[Option[MatchingAndSuitabilityUserAnswers]] = {
    val modifier     = Updates.set("updatedAt", LocalDateTime.now())
    val updateOption = new FindOneAndUpdateOptions()
      .upsert(false)

    collection.findOneAndUpdate(selector(internalId), modifier, updateOption).toFutureOption()
  }

  override def set(userAnswers: MatchingAndSuitabilityUserAnswers): Future[Boolean] = {

    val find          = selector(userAnswers.internalId)
    val updatedObject = userAnswers.copy(updatedAt = LocalDateTime.now)
    val options       = ReplaceOptions().upsert(true)

    collection.replaceOne(find, updatedObject, options).headOption().map(_.exists(_.wasAcknowledged()))
  }

}

trait CacheRepository {

  def get(internalId: String): Future[Option[MatchingAndSuitabilityUserAnswers]]

  def set(userAnswers: MatchingAndSuitabilityUserAnswers): Future[Boolean]
}
