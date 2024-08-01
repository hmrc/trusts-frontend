/*
 * Copyright 2024 HM Revenue & Customs
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

import config.FrontendAppConfig
import models.core.http._
import models.requests.RegistrationDataRequest
import play.api.Logging
import play.api.http.HeaderNames
import play.api.http.Status.{CONFLICT, OK}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClientV2, config: FrontendAppConfig) extends Logging {

  val registrationUrl = s"${config.trustsUrl}/trusts/register"
  val matchingUrl = s"${config.trustsUrl}/trusts/check"

  def register(registrationJson: JsValue, draftId: String)
              (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {
    implicit val newHc: HeaderCarrier = hc.withExtraHeaders(
      Headers.DraftRegistrationId -> draftId,
      Headers.TrueUserAgent -> request.headers.get(HeaderNames.USER_AGENT).getOrElse("No user agent provided")
    )

    http.post(url"$registrationUrl")(newHc)
      .withBody(registrationJson)
      .execute[TrustResponse](TrustResponse.httpReads, ec)
//      .map { response =>
//        logger.info(s"Response status received from trusts api: ${response.status}")
//
//        response.status match {
//          case OK =>
//            response.json.as[RegistrationTRNResponse]
//          case CONFLICT =>
//            TrustResponse.AlreadyRegistered
//          case _ =>
//            TrustResponse.InternalServerError
//        }
//      }
  }

  def matching(matchData: MatchData)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[MatchedResponse] =
    http.post(url"$matchingUrl")
      .withBody(Json.toJson(matchData))
      .execute[MatchedResponse](MatchedResponse.httpReads, ec)
//      .map { response =>
//        logger.info(s"Response status received from trusts api: ${response.status}")
//
//        response.status match {
//          case OK =>
//            response.json.as[SuccessOrFailureResponse]
//          case CONFLICT =>
//            MatchedResponse.AlreadyRegistered
//          case _ =>
//            MatchedResponse.InternalServerError
//        }
//      }

}
