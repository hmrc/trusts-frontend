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

package pages.register

import base.RegistrationSpecBase
import models.registration.pages.TagStatus._
import models.{TaskStatuses, FirstTaxYearAvailable}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationProgressSpec extends RegistrationSpecBase with ScalaCheckPropertyChecks {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  def mockFirstTaxYearAvailable(yearsAgo: Int = 4): FirstTaxYearAvailable = FirstTaxYearAvailable(yearsAgo, earlierYearsToDeclare = false)

  "RegistrationProgress" when {

    ".isTaskListComplete" when {

      "all entities marked as complete" must {
        "return true for isTaskListComplete" in {

          when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
            .thenReturn(Future.successful(TaskStatuses.withAllComplete))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.isTaskListComplete(
            draftId = fakeDraftId,
            firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
            isTaxable = true,
            isExistingTrust = false
          ), Duration.Inf)

          result mustBe true
        }
      }

      "any entity marked as incomplete" must {
        "return false for isTaskListComplete" in {

          when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
            .thenReturn(Future.successful(TaskStatuses()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.isTaskListComplete(
            draftId = fakeDraftId,
            firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
            isTaxable = true,
            isExistingTrust = false
          ), Duration.Inf)

          result mustBe false
        }
      }
    }

    ".taxLiabilityLinkDisplay" must {

      "enable tax liability" when {
        "trust is taxable, non-existing, and first tax year is more than 0 years ago" in {

          forAll(arbitrary[Int].suchThat(_ > 0), arbitrary[Boolean]) { (yearsAgo, earlierYearsToDeclare) =>
            val result = RegistrationProgress.taxLiabilityLinkDisplay(
              firstTaxYearAvailable = Some(FirstTaxYearAvailable(yearsAgo, earlierYearsToDeclare)),
              isTaxable = true,
              isExistingTrust = false
            )
            result mustBe EnableTask
          }
        }
      }

      "disable tax liability" when {

        "trust is taxable, non-existing, but first tax year is 0 years ago" in {

          forAll(arbitrary[Boolean]) { earlierYearsToDeclare =>
            val result = RegistrationProgress.taxLiabilityLinkDisplay(
              firstTaxYearAvailable = Some(FirstTaxYearAvailable(0, earlierYearsToDeclare)),
              isTaxable = true,
              isExistingTrust = false
            )
            result mustBe DisableTask
          }
        }

        "trust start date not found" in {

          val result = RegistrationProgress.taxLiabilityLinkDisplay(
            firstTaxYearAvailable = None,
            isTaxable = true,
            isExistingTrust = false
          )
          result mustBe DisableTask
        }
      }

      "hide tax liability" when {

        "non-taxable" in {

          forAll(arbitrary[Int], arbitrary[Boolean], arbitrary[Boolean]) { (yearsAgo, earlierYearsToDeclare, isExistingTrust) =>
            val result = RegistrationProgress.taxLiabilityLinkDisplay(
              firstTaxYearAvailable = Some(FirstTaxYearAvailable(yearsAgo, earlierYearsToDeclare)),
              isTaxable = false,
              isExistingTrust = isExistingTrust
            )
            result mustBe HideTask
          }
        }

        "an existing trust" in {

          forAll(arbitrary[Int], arbitrary[Boolean], arbitrary[Boolean]) { (yearsAgo, earlierYearsToDeclare, isTaxable) =>
            val result = RegistrationProgress.taxLiabilityLinkDisplay(
              firstTaxYearAvailable = Some(FirstTaxYearAvailable(yearsAgo, earlierYearsToDeclare)),
              isTaxable = isTaxable,
              isExistingTrust = true
            )
            result mustBe HideTask
          }
        }
      }
    }

    ".item" must {

      "render trustee, settlor, beneficiary, protector and other individual details" in {

        when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
          .thenReturn(Future.successful(TaskStatuses()))

        val application = applicationBuilder().build()
        val registrationProgress = application.injector.instanceOf[RegistrationProgress]

        val result = Await.result(registrationProgress.items(
          draftId = fakeDraftId
        ), Duration.Inf)

        result mustBe List(
          Task(Link("trustees", fakeFrontendAppConfig.trusteesFrontendUrl(fakeDraftId)), NotStarted),
          Task(Link("settlors", fakeFrontendAppConfig.settlorsFrontendUrl(fakeDraftId)), NotStarted),
          Task(Link("beneficiaries", fakeFrontendAppConfig.beneficiariesFrontendUrl(fakeDraftId)), NotStarted),
          Task(Link("protectors", fakeFrontendAppConfig.protectorsFrontendUrl(fakeDraftId)), NotStarted),
          Task(Link("otherIndividuals", fakeFrontendAppConfig.otherIndividualsFrontendUrl(fakeDraftId)), NotStarted)
        )
      }
    }

    ".additionalItems" when {

      "taxable" when {

        "new trust" when {

          "trustDetails is not completed" must {
            "render taxLiability as CannotStartYet" in {

              when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
                .thenReturn(Future.successful(TaskStatuses()))

              val application = applicationBuilder().build()
              val registrationProgress = application.injector.instanceOf[RegistrationProgress]

              val result = Await.result(registrationProgress.additionalItems(
                draftId = fakeDraftId,
                firstTaxYearAvailable = None,
                isTaxable = true,
                isExistingTrust = false
              ), Duration.Inf)

              result mustBe List(
                Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), NotStarted),
                Task(Link("assets", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)), NotStarted),
                Task(Link("taxLiability", fakeFrontendAppConfig.taxLiabilityFrontendUrl(fakeDraftId)), CannotStartYet)
              )
            }
          }

          "trustDetails is completed" when {

            "start date >0 tax years ago" when {

              "taxLiability not started" must {
                "render taxLiability as NotStarted" in {

                  when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
                    .thenReturn(Future.successful(TaskStatuses(trustDetails = Completed)))

                  val application = applicationBuilder().build()
                  val registrationProgress = application.injector.instanceOf[RegistrationProgress]

                  val result = Await.result(registrationProgress.additionalItems(
                    draftId = fakeDraftId,
                    firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
                    isTaxable = true,
                    isExistingTrust = false
                  ), Duration.Inf)

                  result mustBe List(
                    Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), Completed),
                    Task(Link("assets", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)), NotStarted),
                    Task(Link("taxLiability", fakeFrontendAppConfig.taxLiabilityFrontendUrl(fakeDraftId)), NotStarted)
                  )
                }
              }

              "taxLiability started" must {
                "render taxLiability as InProgress" in {

                  when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
                    .thenReturn(Future.successful(TaskStatuses(trustDetails = Completed, taxLiability = InProgress)))

                  val application = applicationBuilder().build()
                  val registrationProgress = application.injector.instanceOf[RegistrationProgress]

                  val result = Await.result(registrationProgress.additionalItems(
                    draftId = fakeDraftId,
                    firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
                    isTaxable = true,
                    isExistingTrust = false
                  ), Duration.Inf)

                  result mustBe List(
                    Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), Completed),
                    Task(Link("assets", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)), NotStarted),
                    Task(Link("taxLiability", fakeFrontendAppConfig.taxLiabilityFrontendUrl(fakeDraftId)), InProgress)
                  )
                }
              }
            }

            "start date 0 tax years ago" must {
              "render tax liability as NoActionNeeded" in {

                when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
                  .thenReturn(Future.successful(TaskStatuses(trustDetails = Completed)))

                val application = applicationBuilder().build()
                val registrationProgress = application.injector.instanceOf[RegistrationProgress]

                val result = Await.result(registrationProgress.additionalItems(
                  draftId = fakeDraftId,
                  firstTaxYearAvailable = Some(mockFirstTaxYearAvailable(yearsAgo = 0)),
                  isTaxable = true,
                  isExistingTrust = false
                ), Duration.Inf)

                result mustBe List(
                  Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), Completed),
                  Task(Link("assets", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)), NotStarted),
                  Task(Link("taxLiability", fakeFrontendAppConfig.taxLiabilityFrontendUrl(fakeDraftId)), NoActionNeeded)
                )
              }
            }
          }
        }

        "existing trust" must {
          "not render tax liability" in {
            when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
              .thenReturn(Future.successful(TaskStatuses()))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.additionalItems(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
              isTaxable = true,
              isExistingTrust = true
            ), Duration.Inf)

            result mustBe List(
              Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), NotStarted),
              Task(Link("assets", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)), NotStarted)
            )
          }
        }
      }

      "non-taxable" must {
        "not render assets or tax liability but must render non-EEA business asset" in {

          when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
            .thenReturn(Future.successful(TaskStatuses()))

          val application = applicationBuilder().build()
          val registrationProgress = application.injector.instanceOf[RegistrationProgress]

          val result = Await.result(registrationProgress.additionalItems(
            draftId = fakeDraftId,
            firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
            isTaxable = false,
            isExistingTrust = false
          ), Duration.Inf)

          result mustBe List(
            Task(Link("trustDetails", fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)), NotStarted),
            Task(
              link = Link("companyOwnershipOrControllingInterest", fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)),
              tag = NotStarted,
              appTaskStyles = Some(Width("80%").toString),
              taskTagTextStyles =Some(Width("80%").toString))
          )
        }
      }
    }

    ".taskCount" when {

      "taxable" when {

        "no tasks are completed" must {
          "return (0,8) for tasks completed when tax liability is enabled" in {

            when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
              .thenReturn(Future.successful(TaskStatuses()))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.taskCount(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
              isTaxable = true,
              isExistingTrust = false
            ), Duration.Inf)

            result mustBe(0, 8)
          }

          "return (0,7) for tasks completed when tax liability is not enabled" in {

            when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
              .thenReturn(Future.successful(TaskStatuses()))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.taskCount(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable(0)),
              isTaxable = true,
              isExistingTrust = false
            ), Duration.Inf)

            result mustBe(0, 8)
          }
        }

        "some tasks are completed" must {
          "return correct count of completed tasks" in {

            val completedStatuses = TaskStatuses(
              beneficiaries = Completed,
              trustees = Completed,
              taxLiability = NotStarted,
              protectors = InProgress,
              other = NotStarted,
              trustDetails = Completed,
              settlors = NotStarted,
              assets = Completed
            )

            when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
              .thenReturn(Future.successful(completedStatuses))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.taskCount(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
              isTaxable = true,
              isExistingTrust = false
            ), Duration.Inf)

            result mustBe(4, 8)
          }
        }

        "all tasks are completed" must {
          "return (8,8) for a fully completed registration" in {

            when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
              .thenReturn(Future.successful(TaskStatuses.withAllComplete))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.taskCount(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
              isTaxable = true,
              isExistingTrust = false
            ), Duration.Inf)

            result mustBe(8, 8)
          }
        }
      }

      "non-taxable" when {

        "no tasks are completed" must {
          "return (0,7) as tax liability not included" in {

            when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
              .thenReturn(Future.successful(TaskStatuses()))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.taskCount(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
              isTaxable = false,
              isExistingTrust = false
            ), Duration.Inf)

            result mustBe(0, 7)
          }
        }

        "all tasks are complete" must {
          "return (7,7) as tax liability is not included" in {

            when(mockTrustsStoreService.getTaskStatuses(any())(any(), any()))
              .thenReturn(Future.successful(TaskStatuses.withAllComplete))

            val application = applicationBuilder().build()
            val registrationProgress = application.injector.instanceOf[RegistrationProgress]

            val result = Await.result(registrationProgress.taskCount(
              draftId = fakeDraftId,
              firstTaxYearAvailable = Some(mockFirstTaxYearAvailable()),
              isTaxable = false,
              isExistingTrust = false
            ), Duration.Inf)

            result mustBe(7, 7)
          }
        }
      }
    }
  }
}
