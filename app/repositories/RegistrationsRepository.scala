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

package repositories

import akka.stream.Materializer
import connector.SubmissionDraftConnector
import javax.inject.Inject
import models.SubmissionDraftData
import models.core.UserAnswers
import models.registration.pages.RegistrationStatus
import pages.register.agents.AgentInternalReferencePage
import play.api.Configuration
import play.api.http.Status
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.http.HeaderCarrier
import utils.DateFormatter
import viewmodels.DraftRegistration

import scala.concurrent.{ExecutionContext, Future}

class DefaultRegistrationsRepository @Inject()(
                                          mongo: ReactiveMongoApi,
                                          config: Configuration,
                                          dateFormatter: DateFormatter,
                                          submissionDraftConnector: SubmissionDraftConnector
                                        )(implicit ec: ExecutionContext, m: Materializer) extends RegistrationsRepository {


  private val section = "main"

  private val collectionName: String = "user-answers"

  private val cacheTtl = config.get[Int]("mongodb.registration.ttlSeconds")

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private val createdAtIndex = Index(
    key = Seq("createdAt" -> IndexType.Ascending),
    name = Some("user-answers-created-at-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  private val internalAuthIdIndex = Index(
    key = Seq("internalId" -> IndexType.Ascending),
    name = Some("internal-auth-id-index")
  )

    val started : Future[Unit] = {
      Future.sequence {
        Seq(
          collection.map(_.indexesManager.ensure(createdAtIndex)),
          collection.map(_.indexesManager.ensure(internalAuthIdIndex))
        )
      }.map(_ => ())
    }

  override def get(draftId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    submissionDraftConnector.getDraftSection(draftId, section).map {
      response => Some(response.data.as[UserAnswers])
    }
  }

  override def listDrafts()(implicit hc: HeaderCarrier) : Future[List[DraftRegistration]] = {
    submissionDraftConnector.getCurrentDraftIds().map {
      draftIds =>
        draftIds.flatMap {
          x => x.reference.map {
            reference => DraftRegistration(x.draftId, reference, dateFormatter.savedUntil(x.createdAt))
          }
        }
    }
  }

  override def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {

    val reference =
        userAnswers.get(AgentInternalReferencePage).map {
          reference => reference
        }.orElse(None)

    val draftData = SubmissionDraftData(Json.toJson(userAnswers), reference)

    submissionDraftConnector.setDraftSection(userAnswers.draftId, section, draftData).map {
      response => response.status == Status.OK
    }
  }
}

trait RegistrationsRepository {

  val started: Future[Unit]

  def get(draftId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean]

  def listDrafts()(implicit hc: HeaderCarrier) : Future[List[DraftRegistration]]
}
