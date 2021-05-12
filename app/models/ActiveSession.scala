/*
 * Copyright 2021 HM Revenue & Customs
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

package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{OWrites, Reads, __}

import java.time.LocalDateTime

case class ActiveSession(internalId: String,
                         identifier: String,
                         updatedAt: LocalDateTime = LocalDateTime.now)

object ActiveSession {

  implicit lazy val reads: Reads[ActiveSession] = (
    (__ \ "internalId").read[String] and
      (__ \ "identifier").read[String] and
      (__ \ "updatedAt").read(MongoDateTimeFormats.localDateTimeRead)
    )(ActiveSession.apply _)

  implicit lazy val writes: OWrites[ActiveSession] = (
    (__ \ "internalId").write[String] and
      (__ \ "identifier").write[String] and
      (__ \ "updatedAt").write(MongoDateTimeFormats.localDateTimeWrite)
    )(unlift(ActiveSession.unapply))

}
