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
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

sealed trait EnrolmentsResponse

object EnrolmentsResponse {

  implicit val EnrolmentsFormat: Format[AgentTrusts] = Json.format[AgentTrusts]

  case class AgentTrusts(principalUserIds: Seq[String], delegatedUserIds: Seq[String]) extends EnrolmentsResponse
  case object ServiceUnavailable extends EnrolmentsResponse
  case object ServerError extends EnrolmentsResponse

  implicit lazy val httpReads: HttpReads[EnrolmentsResponse] =
    new HttpReads[EnrolmentsResponse] {
      override def read(method: String, url: String, response: HttpResponse): EnrolmentsResponse = {
        Logger.info(s"[Enrolments] response status received from ES0 api: ${response.status}, body :${response.body}")

        response.status match {
          case OK =>
            response.json.as[AgentTrusts]
          case SERVICE_UNAVAILABLE =>
            ServiceUnavailable
          case _ =>
            ???
        }
      }
    }

}
