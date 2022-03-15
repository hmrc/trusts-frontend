/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.Logging
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

trait MatchedResponse

final case class SuccessOrFailureResponse(`match`: Boolean) extends MatchedResponse

object SuccessOrFailureResponse {
  implicit val formats: Format[SuccessOrFailureResponse] = Json.format[SuccessOrFailureResponse]
}

object MatchedResponse extends Logging {

  case object AlreadyRegistered extends MatchedResponse
  case object InternalServerError extends MatchedResponse

  implicit lazy val httpReads: HttpReads[MatchedResponse] = new HttpReads[MatchedResponse] {
    override def read(method: String, url: String, response: HttpResponse): MatchedResponse = {
      logger.info(s"response status received from trusts api: ${response.status}")

      response.status match {
        case OK =>
          response.json.as[SuccessOrFailureResponse]
        case CONFLICT =>
          AlreadyRegistered
        case _ =>
          InternalServerError
      }
    }
  }
}
