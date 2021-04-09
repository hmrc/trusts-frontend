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

import com.google.inject.ImplementedBy
import config.FrontendAppConfig
import models.{TrustsAuthInternalServerError, TrustsAuthResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TrustsAuthConnectorImpl])
trait TrustsAuthConnector {

  def authoriseAccessCode(draftId: String, accessCode: String)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse]
}

class TrustsAuthConnectorImpl @Inject()(http: HttpClient, config: FrontendAppConfig) extends TrustsAuthConnector {

  private val baseUrl: String = config.trustsAuthUrl + "/trusts-auth"

  override def authoriseAccessCode(draftId: String, accessCode: String)
                                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse] = {
    http.POST[String, TrustsAuthResponse](s"$baseUrl/$draftId/access-code-for-non-taxable", accessCode) recoverWith {
      case _ => Future.successful(TrustsAuthInternalServerError)
    }
  }
}
