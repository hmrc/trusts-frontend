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

///*
// * Copyright 2022 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package repositories
//
//import models.MongoDateTimeFormats
//import play.api.libs.json.{JsObject, Json, Reads}
//import play.api.{Configuration, Logging}
//import play.modules.reactivemongo.ReactiveMongoApi
//import reactivemongo.api.WriteConcern
//import reactivemongo.api.bson.BSONDocument
//import reactivemongo.api.bson.collection.BSONSerializationPack
//import reactivemongo.api.indexes.Index.Aux
//import reactivemongo.api.indexes.{Index, IndexType}
//import reactivemongo.play.json.collection.Helpers.idWrites
//import reactivemongo.play.json.collection.JSONCollection
//
//import java.time.LocalDateTime
//import scala.concurrent.{ExecutionContext, Future}
//
//trait IndexesManager extends Logging {
//
//  val mongo: ReactiveMongoApi
//
//  val config: Configuration
//
//  implicit val ec: ExecutionContext
//
//  val collectionName: String
//
//  val cacheTtl: Int
//
//  val lastUpdatedIndexName: String
//
//  def idIndex: Aux[BSONSerializationPack.type]
//
//  def collection: Future[JSONCollection] =
//    for {
//      _ <- ensureIndexes
//      res <- mongo.database.map(_.collection[JSONCollection](collectionName))
//    } yield res
//
//  private def ensureIndexes: Future[Boolean] = {
//
//    lazy val lastUpdatedIndex: Aux[BSONSerializationPack.type] = Index.apply(BSONSerializationPack)(
//      key = Seq("updatedAt" -> IndexType.Ascending),
//      name = Some(lastUpdatedIndexName),
//      expireAfterSeconds = Some(cacheTtl),
//      options = BSONDocument.empty,
//      unique = false,
//      background = false,
//      dropDups = false,
//      sparse = false,
//      version = None,
//      partialFilter = None,
//      storageEngine = None,
//      weights = None,
//      defaultLanguage = None,
//      languageOverride = None,
//      textIndexVersion = None,
//      sphereIndexVersion = None,
//      bits = None,
//      min = None,
//      max = None,
//      bucketSize = None,
//      collation = None,
//      wildcardProjection = None
//    )
//
//    for {
//      collection              <- mongo.database.map(_.collection[JSONCollection](collectionName))
//      createdLastUpdatedIndex <- collection.indexesManager.ensure(lastUpdatedIndex)
//      createdIdIndex          <- collection.indexesManager.ensure(idIndex)
//    } yield createdLastUpdatedIndex && createdIdIndex
//  }
//
//  def findCollectionAndUpdate[T](selector: JsObject)(implicit rds: Reads[T]): Future[Option[T]] = {
//
//    val modifier = Json.obj(
//      "$set" -> Json.obj(
//        "updatedAt" -> MongoDateTimeFormats.localDateTimeWrite.writes(LocalDateTime.now)
//      )
//    )
//
//    for {
//      col <- collection
//      r <- col.findAndUpdate(
//        selector = selector,
//        update = modifier,
//        fetchNewObject = true,
//        upsert = false,
//        sort = None,
//        fields = None,
//        bypassDocumentValidation = false,
//        writeConcern = WriteConcern.Default,
//        maxTime = None,
//        collation = None,
//        arrayFilters = Nil
//      )
//    } yield r.result[T]
//  }
//
//}
