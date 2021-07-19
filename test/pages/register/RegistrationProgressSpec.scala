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
import models.FirstTaxYearAvailable
import models.RegistrationSubmission.AllStatus
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.{Link, Task}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationProgressSpec extends RegistrationSpecBase with ScalaCheckPropertyChecks {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  
  val mockFirstTaxYearAvailable: FirstTaxYearAvailable = FirstTaxYearAvailable(4, earlierYearsToDeclare = false)

  "RegistrationProgress" when {

    ".isTaskListComplete" when {

      "all entities marked as complete" must {
        "return true for isTaskListComplete" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus.withAllComplete))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.isTaskListComplete(
            draftId = fakeDraftId,
            firstTaxYearAvailable = Some(mockFirstTaxYearAvailable),
            isTaxable = true,
            isExistingTrust = false
          ), Duration.Inf)

          result mustBe true
        }
      }

      "any entity marked as incomplete" must {
        "return false for isTaskListComplete" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.isTaskListComplete(
            draftId = fakeDraftId,
            firstTaxYearAvailable = Some(mockFirstTaxYearAvailable),
            isTaxable = true,
            isExistingTrust = false
          ), Duration.Inf)

          result mustBe false
        }
      }
    }

    ".items" when {

      "taxable" when {

        "new trust" must {
          "render all items" in {

            when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.items(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable),
              isTaxable = true,
              isExistingTrust = false
            ), Duration.Inf)

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

        "existing trust" must {
          "not render tax liability" in {

            when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.items(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable),
              isTaxable = true,
              isExistingTrust = true
            ), Duration.Inf)

            result mustBe List(
              Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), None),
              Task(Link("settlors", fakeFrontendAppConfig.settlorsFrontendUrl(fakeDraftId)), None),
              Task(Link("trustees", fakeFrontendAppConfig.trusteesFrontendUrl(fakeDraftId)), None),
              Task(Link("beneficiaries", fakeFrontendAppConfig.beneficiariesFrontendUrl(fakeDraftId)), None),
              Task(Link("assets", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)), None)
            )
          }
        }
      }

      "non-taxable" must {
        "not render assets or tax liability" in {

          when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.items(
            draftId = fakeDraftId,
            firstTaxYearAvailable = Some(mockFirstTaxYearAvailable),
            isTaxable = false,
            isExistingTrust = false
          ), Duration.Inf)

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

    ".showTaxLiability" must {

      "show tax liability" when {
        "trust is taxable, non-existing, and first tax year is more than 0 years ago" in {

          forAll(arbitrary[Int].suchThat(_ > 0), arbitrary[Boolean]) { (yearsAgo, earlierYearsToDeclare) =>
            val result = RegistrationProgress.showTaxLiability(
              firstTaxYearAvailable = Some(FirstTaxYearAvailable(yearsAgo, earlierYearsToDeclare)),
              isTaxable = true,
              isExistingTrust = false
            )
            result mustBe true
          }
        }
      }

      "not show tax liability" when {
        "non-taxable" in {

          forAll(arbitrary[Int], arbitrary[Boolean], arbitrary[Boolean]) { (yearsAgo, earlierYearsToDeclare, isExistingTrust) =>
            val result = RegistrationProgress.showTaxLiability(
              firstTaxYearAvailable = Some(FirstTaxYearAvailable(yearsAgo, earlierYearsToDeclare)),
              isTaxable = false,
              isExistingTrust = isExistingTrust
            )
            result mustBe false
          }
        }

        "an existing trust" in {

          forAll(arbitrary[Int], arbitrary[Boolean], arbitrary[Boolean]) { (yearsAgo, earlierYearsToDeclare, isTaxable) =>
            val result = RegistrationProgress.showTaxLiability(
              firstTaxYearAvailable = Some(FirstTaxYearAvailable(yearsAgo, earlierYearsToDeclare)),
              isTaxable = isTaxable,
              isExistingTrust = true
            )
            result mustBe false
          }
        }

        "trust is taxable, non-existing, but first tax year is 0 years ago" in {

          forAll(arbitrary[Boolean]) { earlierYearsToDeclare =>
            val result = RegistrationProgress.showTaxLiability(
              firstTaxYearAvailable = Some(FirstTaxYearAvailable(0, earlierYearsToDeclare)),
              isTaxable = true,
              isExistingTrust = false
            )
            result mustBe false
          }
        }

        "trust start date not found" in {

          forAll(arbitrary[Boolean], arbitrary[Boolean]) { (isTaxable, isExistingTrust) =>
            val result = RegistrationProgress.showTaxLiability(
              firstTaxYearAvailable = None,
              isTaxable = isTaxable,
              isExistingTrust = isExistingTrust
            )
            result mustBe false
          }
        }
      }
    }
  }
}
