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

import models.core.UserAnswerImplicits._
import play.api.Logging
import play.api.libs.json._
import queries.{Gettable, Settable}

import scala.util.{Failure, Success, Try}

trait TrustsFrontendUserAnswers[U <: TrustsFrontendUserAnswers[U]] extends Logging {

  val data: JsObject

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.at(page.path).reads(data) match {
      case JsSuccess(value, _) => Some(value)
      case JsError(_)          => None
    }

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[U]

  def remove[A](page: Settable[A]): Try[U]

  def updatedDataForSet[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[JsObject] =
    data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors)       =>
        val errorPaths = errors.collectFirst { case (path, e) => s"$path $e" }
        logger.warn(s"Unable to set path ${page.path} due to errors $errorPaths")
        Failure(JsResultException(errors))
    }

  def updatedDataForRemove[A](page: Settable[A]): Try[JsObject] =
    data.removeObject(page.path) match {
      case JsSuccess(jsValue, _) => Success(jsValue)
      case JsError(_)            => Success(data)
    }

}
