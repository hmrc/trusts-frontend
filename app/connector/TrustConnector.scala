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

package connector

import config.FrontendAppConfig
import models.core.http.*
import models.requests.RegistrationDataRequest
import play.api.Logging
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject() (http: HttpClientV2, config: FrontendAppConfig) extends Logging {

  val registrationUrl = s"${config.trustsUrl}/trusts/register"
  val matchingUrl     = s"${config.trustsUrl}/trusts/check"

  def register(registrationJson: JsValue, draftId: String)(using
    request: RegistrationDataRequest[?],
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[TrustResponse] = {
    given newHc: HeaderCarrier = hc.withExtraHeaders(
      Headers.DraftRegistrationId -> draftId,
      Headers.TrueUserAgent       -> request.headers.get(HeaderNames.USER_AGENT).getOrElse("No user agent provided")
    )

    http
      .post(url"$registrationUrl")(using newHc)
      .withBody(registrationJson)
      .execute[TrustResponse](using TrustResponse.httpReads, ec)
  }

  def matching(matchData: MatchData)(using hc: HeaderCarrier, ec: ExecutionContext): Future[MatchedResponse] =
    http
      .post(url"$matchingUrl")
      .withBody(Json.toJson(matchData)(using MatchData.writes))
      .execute[MatchedResponse](using MatchedResponse.httpReads, ec)

}
