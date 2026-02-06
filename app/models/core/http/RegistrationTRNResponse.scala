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

package models.core.http

import auditing.RegistrationErrorAuditEvent
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

trait TrustResponse

final case class RegistrationTRNResponse(trn: String) extends TrustResponse

object RegistrationTRNResponse {

  implicit val formats: OFormat[RegistrationTRNResponse] = Json.format[RegistrationTRNResponse]

}

object TrustResponse extends Logging {

  implicit object RegistrationResponseFormats extends Format[TrustResponse] {

    override def reads(json: JsValue): JsResult[TrustResponse] = json.validate[RegistrationTRNResponse]

    override def writes(o: TrustResponse): JsValue = o match {
      case x: RegistrationTRNResponse     => Json.toJson(x)(RegistrationTRNResponse.formats)
      case x: RegistrationErrorAuditEvent => Json.toJson(x)(RegistrationErrorAuditEvent.formats)
    }

  }

  case object AlreadyRegistered extends TrustResponse
  case object InternalServerError extends TrustResponse

  final case class UnableToRegister() extends Exception with TrustResponse

  implicit lazy val httpReads: HttpReads[TrustResponse] = new HttpReads[TrustResponse] {
    override def read(method: String, url: String, response: HttpResponse): TrustResponse = {
      logger.info(s"Response status received from trusts api: ${response.status}")

      response.status match {
        case OK       =>
          response.json.as[RegistrationTRNResponse]
        case CONFLICT =>
          AlreadyRegistered
        case _        =>
          InternalServerError
      }
    }
  }

}
