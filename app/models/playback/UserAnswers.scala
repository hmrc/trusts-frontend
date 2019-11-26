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

package models.playback

import java.time.LocalDateTime
import models.{MongoDateTimeFormats, RichJsObject}
import play.api.Logger
import play.api.libs.json._
import queries.{Gettable, Settable}
import scala.util.{Failure, Success, Try}

final case class UserAnswers(
                              internalAuthId: String,
                              data: JsObject = Json.obj(),
                              updatedAt: LocalDateTime = LocalDateTime.now
                            ) {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] = {
    Reads.at(page.path).reads(data) match {
      case JsSuccess(value, _) => Some(value)
      case JsError(errors) =>
        Logger.info(s"[UserAnswers] tried to read path ${page.path} errors: $errors")
        None
    }
  }

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        val errorPaths = errors.collectFirst{ case (path, e) => s"$path $e"}
        Logger.warn(s"[UserAnswers] unable to set path ${page.path} due to errors $errorPaths")
        Failure(JsResultException(errors))
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        page.cleanup(Some(value), updatedAnswers)
    }
  }

  def remove[A](query: Settable[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(query.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        query.cleanup(None, updatedAnswers)
    }
  }
}

object UserAnswers {

  implicit lazy val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "internalId").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "updatedAt").read(MongoDateTimeFormats.localDateTimeRead)
      ) (UserAnswers.apply _)
  }

  implicit lazy val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "internalId").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "updatedAt").write(MongoDateTimeFormats.localDateTimeWrite)
      ) (unlift(UserAnswers.unapply))
  }
}