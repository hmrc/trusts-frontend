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

package models

import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

trait TrustResponse

final case class RegistrationTRNResponse(trn : String) extends TrustResponse
case object AlreadyRegistered extends TrustResponse
case object InternalServerError extends TrustResponse

object TrustResponse {

  implicit val formats = Json.format[RegistrationTRNResponse]

  implicit lazy val httpReads: HttpReads[TrustResponse] =
    new HttpReads[TrustResponse] {
      override def read(method: String, url: String, response: HttpResponse): TrustResponse = {
        Logger.info(s"[TrustResponse]  response status received from trusts api: ${response.status}, body :${response.body}")

        response.status match {
          case OK =>
            response.json.as[RegistrationTRNResponse]
          case CONFLICT =>
            AlreadyRegistered
          case _ =>
            InternalServerError

        }
      }
    }


}
