/*
 * Copyright 2021 HM Revenue & Customs
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
import models.{TaskStatuses, FeatureResponse}
import uk.gov.hmrc.http.HttpReads.Implicits.readFromJson
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustsStoreConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val baseUrl: String = s"${config.trustsStoreUrl}/trusts-store"

  def getFeature(feature: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FeatureResponse] = {
    val url: String = s"$baseUrl/features/$feature"
    http.GET[FeatureResponse](url)
  }

  def getTaskStatuses(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TaskStatuses] = {
    val url: String = s"$baseUrl/register/tasks/$draftId"
    http.GET[TaskStatuses](url)
  }

}

