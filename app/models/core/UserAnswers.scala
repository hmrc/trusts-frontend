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

package models.core

import _root_.pages.register.TrustHaveAUTRPage
import _root_.pages.register.suitability.TrustTaxableYesNoPage
import models.MongoDateTimeFormats
import models.core.UserAnswerImplicits._
import models.registration.pages.RegistrationStatus
import models.registration.pages.RegistrationStatus.NotStarted
import play.api.libs.functional.syntax._
import play.api.libs.json._
import queries.Settable

import java.time.LocalDateTime
import scala.util.{Success, Try}

final case class UserAnswers(draftId: String,
                                         override val data: JsObject = Json.obj(),
                                         progress: RegistrationStatus = NotStarted,
                                         createdAt: LocalDateTime = LocalDateTime.now,
                                         internalAuthId: String) extends TrustsFrontendUserAnswers[UserAnswers] {

  def isTaxable: Boolean = !this.get(TrustTaxableYesNoPage).contains(false)

  def isExistingTrust: Boolean = this.get(TrustHaveAUTRPage).contains(true)

  override def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {
    updatedDataForSet(page, value).flatMap { d =>
      page.cleanup(Some(value), this.copy(data = d))
    }
  }

  override def remove[A](page: Settable[A]): Try[UserAnswers] = {
    updatedDataForRemove(page).flatMap { d =>
      page.cleanup(None, this.copy(data = d))
    }
  }

  def deleteAtPath(path: JsPath): Try[UserAnswers] = {
    data.removeObject(path).map(obj => copy(data = obj)).fold(
      _ => Success(this),
      result => Success(result)
    )
  }
}

object UserAnswers {

  implicit lazy val reads: Reads[UserAnswers] = (
    (__ \ "_id").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "progress").read[RegistrationStatus] and
      (__ \ "createdAt").read(MongoDateTimeFormats.localDateTimeRead) and
      (__ \ "internalId").read[String]
    )(UserAnswers.apply _)

  implicit lazy val writes: Writes[UserAnswers] = (
    (__ \ "_id").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "progress").write[RegistrationStatus] and
      (__ \ "createdAt").write(MongoDateTimeFormats.localDateTimeWrite) and
      (__ \ "internalId").write[String]
    )(unlift(UserAnswers.unapply))
}
