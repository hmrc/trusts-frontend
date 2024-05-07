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

package services

import base.RegistrationSpecBase
import connector.TrustsStoreConnector
import models.TaskStatuses
import models.registration.pages.TagStatus.Completed
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.scalatest.concurrent.ScalaFutures.whenReady
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class TrustsStoreServiceSpec extends RegistrationSpecBase {

  val mockConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

  val trustsStoreService = new TrustsStoreService(mockConnector)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "getTaskStatuses" must {
    "call trusts store connector" in {

      val taskStatuses = TaskStatuses(beneficiaries = Completed)

      when(mockConnector.getTaskStatuses(any())(any(), any()))
        .thenReturn(Future.successful(taskStatuses))

      val result = trustsStoreService.getTaskStatuses(fakeDraftId)

      whenReady(result) { res =>
        res mustEqual taskStatuses
        verify(mockConnector).getTaskStatuses(eqTo(fakeDraftId))(any(), any())
      }
    }
  }
}
