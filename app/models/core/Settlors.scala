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

package models.core

import models.core.http.IdentificationType
import models.core.pages.FullName
import play.api.libs.json.{Format, Json}

import java.time.LocalDate

case class Settlors(settlor: Option[List[Settlor]])

object Settlors {
  implicit val settlorsFormat: Format[Settlors] = Json.format[Settlors]
}

case class Settlor(
  aliveAtRegistration: Option[Boolean],
  name: FullName,
  dateOfBirth: Option[LocalDate],
  identification: Option[IdentificationType],
  countryOfResidence: Option[String],
  nationality: Option[String],
  legallyIncapable: Option[Boolean]
)

object Settlor {
  implicit val settlorFormat: Format[Settlor] = Json.format[Settlor]
}
