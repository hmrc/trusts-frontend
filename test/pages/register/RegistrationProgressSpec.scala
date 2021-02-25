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
import viewmodels.{Link, Task}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationProgressSpec extends RegistrationSpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "RegistrationProgress" when {

    ".isTaskListComplete" when {

      "all entities marked as complete" must {
        "return true for isTaskListComplete" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus.withAllComplete))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.isTaskListComplete(fakeDraftId, isTaxable = true), Duration.Inf)

          result mustBe true
        }
      }

      "any entity marked as incomplete" must {
        "return false for isTaskListComplete" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.isTaskListComplete(fakeDraftId, isTaxable = true), Duration.Inf)

          result mustBe false
        }
      }
    }

    ".items" when {

      "taxable" must {
        "render all items" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.items(fakeDraftId, isTaxable = true), Duration.Inf)

          result mustBe List(
            Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), None),
            Task(Link("settlors", fakeFrontendAppConfig.settlorsFrontendUrl(fakeDraftId)), None),
            Task(Link("trustees", fakeFrontendAppConfig.trusteesFrontendUrl(fakeDraftId)), None),
            Task(Link("beneficiaries", fakeFrontendAppConfig.beneficiariesFrontendUrl(fakeDraftId)), None),
            Task(Link("assets", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)), None),
            Task(Link("taxLiability", fakeFrontendAppConfig.taxLiabilityFrontendUrl(fakeDraftId)), None)
          )
        }
      }

      "non-taxable" must {
        "not render assets or tax liability" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.items(fakeDraftId, isTaxable = false), Duration.Inf)

          result mustBe List(
            Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), None),
            Task(Link("settlors", fakeFrontendAppConfig.settlorsFrontendUrl(fakeDraftId)), None),
            Task(Link("trustees", fakeFrontendAppConfig.trusteesFrontendUrl(fakeDraftId)), None),
            Task(Link("beneficiaries", fakeFrontendAppConfig.beneficiariesFrontendUrl(fakeDraftId)), None)
          )
        }
      }
    }

    ".additionalItems" when {

      "taxable" must {
        "only render protectors and other individuals" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.additionalItems(fakeDraftId, isTaxable = true), Duration.Inf)

          result mustBe List(
            Task(Link("protectors", fakeFrontendAppConfig.protectorsFrontendUrl(fakeDraftId)), None),
            Task(Link("otherIndividuals", fakeFrontendAppConfig.otherIndividualsFrontendUrl(fakeDraftId)), None)
          )
        }
      }

      "non-taxable" must {
        "also render non-EEA business asset" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.additionalItems(fakeDraftId, isTaxable = false), Duration.Inf)

          result mustBe List(
            Task(Link("companyOwnershipOrControllingInterest", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)), None),
            Task(Link("protectors", fakeFrontendAppConfig.protectorsFrontendUrl(fakeDraftId)), None),
            Task(Link("otherIndividuals", fakeFrontendAppConfig.otherIndividualsFrontendUrl(fakeDraftId)), None)
          )
        }
      }
    }
  }
}
