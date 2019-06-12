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

import akka.stream.Materializer
import javax.inject.Inject
import models.{RegistrationProgress, UserAnswers}
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class DefaultSessionRepository @Inject()(
                                          mongo: ReactiveMongoApi,
                                          config: Configuration
                                        )(implicit ec: ExecutionContext, m: Materializer) extends SessionRepository {


  private val collectionName: String = "user-answers"

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private val createdAtIndex = Index(
    key     = Seq("createdAt" -> IndexType.Ascending),
    name    = Some("user-answers-created-at-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  val started: Future[Unit] =
    collection.flatMap {
      _.indexesManager.ensure(createdAtIndex)
    }.map(_ => ())

  override def get(draftId: String, internalId: String): Future[Option[UserAnswers]] = {
    collection.flatMap(_.find(Json.obj("_id" -> draftId, "internalId" -> internalId), None).one[UserAnswers])
  }

  override def getDraftIds(internalId: String): Future[List[UserAnswers]] = {
    val selector = Json.obj(
        "internalId" -> internalId,
        "progress" -> Json.obj("$ne" -> RegistrationProgress.Complete.toString)
      )

    collection.flatMap(
      _.find(
        selector = selector,
        projection = None
      )
        .sort(Json.obj("createdAt" -> -1))
        .cursor[UserAnswers]()
        .collect[List](20, Cursor.FailOnError[List[UserAnswers]]()))
  }

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> userAnswers
    )

    collection.flatMap {
      _.update(ordered = false).one(selector, modifier, upsert = true).map {
        lastError =>
          lastError.ok
      }
    }
  }
}

trait SessionRepository {

  val started: Future[Unit]

  def get(draftId: String, internalId : String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]

  def getDraftIds(internalId: String): Future[List[UserAnswers]]
}
