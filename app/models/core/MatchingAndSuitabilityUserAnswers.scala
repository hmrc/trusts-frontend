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

package models.core

import models.MongoDateTimeFormats
import play.api.libs.functional.syntax._
import play.api.libs.json._
import queries.Settable

import java.time.LocalDateTime
import scala.util.Try

case class MatchingAndSuitabilityUserAnswers(
  internalId: String,
  override val data: JsObject = Json.obj(),
  updatedAt: LocalDateTime = LocalDateTime.now
) extends TrustsFrontendUserAnswers[MatchingAndSuitabilityUserAnswers] {

  override def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[MatchingAndSuitabilityUserAnswers] =
    updatedDataForSet(page, value).flatMap { d =>
      page.cleanup(Some(value), this.copy(data = d))
    }

  override def remove[A](page: Settable[A]): Try[MatchingAndSuitabilityUserAnswers] =
    updatedDataForRemove(page).flatMap { d =>
      page.cleanup(None, this.copy(data = d))
    }

}

object MatchingAndSuitabilityUserAnswers {

  implicit lazy val reads: Reads[MatchingAndSuitabilityUserAnswers] = (
    (__ \ "internalId").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "updatedAt").read(MongoDateTimeFormats.localDateTimeRead)
  )(MatchingAndSuitabilityUserAnswers.apply _)

  implicit lazy val writes: Writes[MatchingAndSuitabilityUserAnswers] = (
    (__ \ "internalId").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "updatedAt").write(MongoDateTimeFormats.localDateTimeWrite)
  )(unlift(MatchingAndSuitabilityUserAnswers.unapply))

}
