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

package pages.register

import base.RegistrationSpecBase
import models.RegistrationSubmission.AllStatus
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationProgressSpec extends RegistrationSpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "RegistrationProgress" when {

    "all entities marked as complete" must {
      "return true for isTaskListComplete" in {

        when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus.withAllComplete))

        val application = applicationBuilder().build()
        val registrationProgress = application.injector.instanceOf[RegistrationProgress]

        Await.result(registrationProgress.isTaskListComplete(fakeDraftId, isTaxable = true), Duration.Inf) mustBe true
      }
    }

    "any entity marked as incomplete" must {
      "return false for isTaskListComplete" in {

        when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

        val application = applicationBuilder().build()
        val registrationProgress = application.injector.instanceOf[RegistrationProgress]

        Await.result(registrationProgress.isTaskListComplete(fakeDraftId, isTaxable = true), Duration.Inf) mustBe false
      }
    }
  }
}
