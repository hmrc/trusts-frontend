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

package connector

import play.api.Logger
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import play.api.libs.json.{Json, OFormat}

case class TrustClaim(utr:String, managedByAgent: Boolean, trustLocked:Boolean)

object TrustClaim {

  implicit val formats : OFormat[TrustClaim] = Json.format[TrustClaim]

  implicit def httpReads(utr : String): HttpReads[Option[TrustClaim]] =
    new HttpReads[Option[TrustClaim]] {
      override def read(method: String, url: String, response: HttpResponse): Option[TrustClaim] = {
        Logger.info(s"[TrustResponse]  response status received from trusts store api: ${response.status}, body :${response.body}")

        response.status match {
          case OK =>
            response.json.asOpt[TrustClaim] match {
              case validClaim @ Some(c) =>
                if (c.utr.toLowerCase.trim == utr.toLowerCase.trim) {
                  validClaim
                } else {
                  None
                }
              case None => None
            }
          case _ =>
            None
        }
      }
    }


}