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

package models.playback.http

import play.api.Logger
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

sealed trait PlaybackResponse

case class Trust(playback: JsValue) extends PlaybackResponse
case object PlaybackNotFound extends PlaybackResponse
case object PlaybackServiceUnavailable extends PlaybackResponse
case object PlaybackServerError extends PlaybackResponse

object PlaybackResponse {

  implicit lazy val httpReads: HttpReads[PlaybackResponse] =
    new HttpReads[PlaybackResponse] {
      override def read(method: String, url: String, response: HttpResponse): PlaybackResponse = {
        Logger.info(s"[PlaybackResponse] response status received from trusts api: ${response.status}")

        response.status match {
          case OK =>
            Trust(response.json)
          case NOT_FOUND =>
            PlaybackNotFound
          case SERVICE_UNAVAILABLE =>
            PlaybackServiceUnavailable
          case _ =>
            PlaybackServerError
        }
      }
    }


}
