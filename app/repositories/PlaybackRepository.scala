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
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.WriteError
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import utils.DateFormatter

import scala.concurrent.{ExecutionContext, Future}

class DefaultPlaybackRepository @Inject()(
                                          mongo: ReactiveMongoApi,
                                          config: Configuration,
                                          dateFormatter: DateFormatter
                                        )(implicit ec: ExecutionContext, m: Materializer) extends PlaybackRepository {


  private val collectionName: String = "playback"

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))


  private val internalAuthIdIndex = Index(
    key = Seq("internalId" -> IndexType.Ascending),
    name = Some("internal-auth-id-index")
  )

    val started : Future[Unit] = {
      Future.sequence {
        Seq(
          collection.map(_.indexesManager.ensure(internalAuthIdIndex))
        )
      }.map(_ => ())
    }


  override def store(internalId: String, playback: JsValue): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> internalId
    )

    val modifier = Json.obj(
      "$set" -> playback
    )

    collection.flatMap {
      _.update(ordered = false).one(selector, modifier, upsert = true, multi = false).map {
        result => result.ok
      }
    }
  }
}

trait PlaybackRepository {

  val started: Future[Unit]

  def store(internalId: String, playback: JsValue): Future[Boolean]
}
