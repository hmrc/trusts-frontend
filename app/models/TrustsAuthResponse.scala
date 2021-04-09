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

import play.api.libs.json.{Format, Json, Reads, __}

sealed trait TrustsAuthResponse
object TrustsAuthResponse {
  implicit val reads: Reads[TrustsAuthResponse] =
    __.read[TrustsAuthAllowed].widen[TrustsAuthResponse] orElse
      __.read[TrustsAuthDenied].widen[TrustsAuthResponse]
}

case class TrustsAuthAllowed(authorised: Boolean = true) extends TrustsAuthResponse
case object TrustsAuthAllowed {
  implicit val format: Format[TrustsAuthAllowed] = Json.format[TrustsAuthAllowed]
}

case class TrustsAuthDenied(redirectUrl: String) extends TrustsAuthResponse
case object TrustsAuthDenied {
  implicit val format: Format[TrustsAuthDenied] = Json.format[TrustsAuthDenied]
}

case object TrustsAuthInternalServerError extends TrustsAuthResponse
