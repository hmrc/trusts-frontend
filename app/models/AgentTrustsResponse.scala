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

sealed trait AgentTrustsResponse

object AgentTrustsResponse {

  implicit val format: Format[AgentTrusts] = Json.format[AgentTrusts]

  case class AgentTrusts(principalUserIds: Seq[String], delegatedUserIds: Seq[String]) extends AgentTrustsResponse
  case object NotClaimed extends AgentTrustsResponse
  case object NoContent extends AgentTrustsResponse
  case object ServiceUnavailable extends AgentTrustsResponse
  case object Forbidden extends AgentTrustsResponse
  case object BadRequest extends AgentTrustsResponse
  case object ServerError extends AgentTrustsResponse
  case object Claimed extends AgentTrustsResponse

  implicit lazy val httpReads: HttpReads[AgentTrustsResponse] =
    new HttpReads[AgentTrustsResponse] {
      override def read(method: String, url: String, response: HttpResponse): AgentTrustsResponse = {
        Logger.info(s"[AgentTrusts] response status received from ES0 api: ${response.status}, body :${response.body}")

        response.status match {
          case OK =>
            response.json.as[AgentTrusts] match {
                case AgentTrusts(Seq(), _) => NotClaimed
                case agentTrusts => if (agentTrusts.principalUserIds.nonEmpty) agentTrusts else Claimed
            }
          case NO_CONTENT =>
            NoContent
          case SERVICE_UNAVAILABLE =>
            ServiceUnavailable
          case FORBIDDEN =>
            Forbidden
          case BAD_REQUEST =>
            BadRequest
          case _ =>
            ServerError
        }
      }
    }
}
