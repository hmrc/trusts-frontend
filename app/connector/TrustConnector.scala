/*
 * Copyright 2023 HM Revenue & Customs
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
import models.core.http.{MatchData, MatchedResponse, TrustResponse}
import models.requests.RegistrationDataRequest
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  val registrationUrl = s"${config.trustsUrl}/trusts/register"

  def register(registrationJson: JsValue, draftId: String)
              (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {

    val newHc: HeaderCarrier = hc.withExtraHeaders(
      Headers.DraftRegistrationId -> draftId,
      Headers.TrueUserAgent -> request.headers.get(HeaderNames.USER_AGENT).getOrElse("No user agent provided")
    )

    http.POST[JsValue, TrustResponse](registrationUrl, registrationJson)(implicitly[Writes[JsValue]], TrustResponse.httpReads, newHc, ec)
  }

  def matching(matchData: MatchData)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[MatchedResponse] = {
    val matchingUrl = s"${config.trustsUrl}/trusts/check"
    http.POST[MatchData, MatchedResponse](matchingUrl, matchData: MatchData)(MatchData.writes, MatchedResponse.httpReads, hc, ec)
  }

}
