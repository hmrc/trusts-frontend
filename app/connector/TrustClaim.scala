/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.language.implicitConversions

case class TrustClaim(utr:String, managedByAgent: Boolean, trustLocked:Boolean)

object TrustClaim extends Logging {

  implicit val formats : OFormat[TrustClaim] = Json.format[TrustClaim]

  implicit def httpReads(utr : String): HttpReads[Option[TrustClaim]] =
    new HttpReads[Option[TrustClaim]] {
      override def read(method: String, url: String, response: HttpResponse): Option[TrustClaim] = {
        logger.info(s"[UTR: $utr] response status received from trusts store api: ${response.status}")

        response.status match {
          case OK =>
            response.json.asOpt[TrustClaim] match {
              case validClaim @ Some(c) =>
                if (c.utr.toLowerCase.trim == utr.toLowerCase.trim) {
                  validClaim
                } else {
                  logger.info(s"[UTR: $utr] UTR could not be matched")
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