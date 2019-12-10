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

sealed trait TrustsResponse

sealed trait TrustStatus extends TrustsResponse

case object Processing extends TrustStatus
case object Closed extends TrustStatus
case class Processed(playback: GetTrust, formBundleNumber : String) extends TrustStatus
case object SorryThereHasBeenAProblem extends TrustStatus
case object UtrNotFound extends TrustsResponse
case object TrustServiceUnavailable extends TrustsResponse
case object ServerError extends TrustsResponse

object TrustsResponse {

  implicit object TrustStatusReads extends Reads[TrustStatus] {
    override def reads(json:JsValue): JsResult[TrustStatus] = json("responseHeader")("status") match {
      case JsString("In Processing") => JsSuccess(Processing)
      case JsString("Closed") => JsSuccess(Closed)
      case JsString("Pending Closure") => JsSuccess(Closed)
      case JsString("Processed") =>
         json("getTrust").validate[GetTrust] match {
          case JsSuccess(trust, _) =>
            val formBundle = json("responseHeader")("formBundleNo").as[String]
            JsSuccess(Processed(trust, formBundle))
          case JsError(errors) => JsError(s"Can not parse as GetTrust due to $errors")
        }
      case JsString("Parked") => JsSuccess(SorryThereHasBeenAProblem)
      case JsString("Obsoleted") => JsSuccess(SorryThereHasBeenAProblem)
      case JsString("Suspended") => JsSuccess(SorryThereHasBeenAProblem)
      case _ => JsError("Unexpected Status")
    }
  }

  implicit lazy val httpReads: HttpReads[TrustsResponse] =
    new HttpReads[TrustsResponse] {
      override def read(method: String, url: String, response: HttpResponse): TrustsResponse = {
        Logger.info(s"[TrustStatus] response status received from trusts status api: ${response.status}")

        response.status match {
          case OK =>
            response.json.as[TrustStatus]
          case NO_CONTENT =>
            SorryThereHasBeenAProblem
          case NOT_FOUND =>
            UtrNotFound
          case SERVICE_UNAVAILABLE =>
            TrustServiceUnavailable
          case _ =>
            ServerError
        }
      }
    }
}
