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

package controllers.register

import base.RegistrationSpecBase
import models.NormalMode
import models.core.UserAnswers
import models.registration.Matched
import navigation.Navigator
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register._
import pages.register.suitability.TrustTaxableYesNoPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import utils.DateFormatter
import viewmodels.Task
import views.html.register.TaskListView

import scala.concurrent.Future

class TaskListControllerSpec extends RegistrationSpecBase with ScalaCheckPropertyChecks with BeforeAndAfterEach {

  private val mockRegistrationProgress: RegistrationProgress = mock[RegistrationProgress]
  private val mockDateFormatter: DateFormatter = mock[DateFormatter]
  private val mockFeatureFlagService: FeatureFlagService = mock[FeatureFlagService]

  private val fakeItems: List[Task] = Nil
  private val fakeAdditionalItems: List[Task] = Nil

  private val savedUntil = "21 April 2021"
  private val utr = "1234567890"

  private val is5mldEnabled: Boolean = true

  override def beforeEach(): Unit = {
    reset(mockRegistrationProgress)

    when(mockRegistrationProgress.items(any(), any(), any(), any())(any()))
      .thenReturn(Future.successful(fakeItems))

    when(mockRegistrationProgress.additionalItems(any(), any())(any()))
      .thenReturn(Future.successful(fakeAdditionalItems))

    when(mockRegistrationProgress.isTaskListComplete(any(), any(), any(), any())(any()))
      .thenReturn(Future.successful(true))

    reset(mockDateFormatter)

    when(mockDateFormatter.savedUntil(any())(any()))
      .thenReturn(savedUntil)

    reset(mockFeatureFlagService)

    when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
      .thenReturn(Future.successful(is5mldEnabled))
  }

  override protected def applicationBuilder(userAnswers: Option[UserAnswers],
                                            affinityGroup: AffinityGroup,
                                            enrolments: Enrolments = Enrolments(Set.empty[Enrolment]),
                                            navigator: Navigator = fakeNavigator): GuiceApplicationBuilder = {

    super.applicationBuilder(userAnswers, affinityGroup)
      .configure(("microservice.services.features.removeTaxLiabilityOnTaskList", false))
      .overrides(
        bind[RegistrationProgress].toInstance(mockRegistrationProgress),
        bind[DateFormatter].toInstance(mockDateFormatter),
        bind[FeatureFlagService].toInstance(mockFeatureFlagService)
      )
  }

  "TaskListController" when {

    "no required answer for TrustRegisteredOnlinePage" must {
      "redirect to RegisteredOnline" in {

        val answers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, fakeDraftId).url

        application.stop()
      }
    }

    "no required answer for TrustHaveAUTRPage" must {
      "redirect to TrustHaveAUTR" in {

        val answers = emptyUserAnswers.set(TrustRegisteredOnlinePage, true).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.TrustHaveAUTRController.onPageLoad(NormalMode, fakeDraftId).url

        application.stop()
      }
    }

    "an existing trust" when {

      "successfully matched" must {
        "return OK and the correct view for a GET" in {

          val answers = emptyUserAnswers
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, utr).success.value
            .set(ExistingTrustMatched, Matched.Success).success.value

          val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = Organisation).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          status(result) mustEqual OK

          val view = application.injector.instanceOf[TaskListView]

          contentAsString(result) mustEqual
            view(
              isTaxable = true,
              draftId = fakeDraftId,
              savedUntil = savedUntil,
              sections = fakeItems,
              additionalSections = fakeAdditionalItems,
              isTaskListComplete = true,
              affinityGroup = Organisation,
              is5mldEnabled = is5mldEnabled
            )(request, messages).toString

          application.stop()
        }
      }

      "has not matched" when {

        "already registered" must {
          "redirect to AlreadyRegistered" in {

            val answers = emptyUserAnswers
              .set(TrustRegisteredOnlinePage, false).success.value
              .set(TrustHaveAUTRPage, true).success.value
              .set(WhatIsTheUTRPage, utr).success.value
              .set(ExistingTrustMatched, Matched.AlreadyRegistered).success.value

            val application = applicationBuilder(userAnswers = Some(answers)).build()

            val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual routes.FailedMatchController.onPageLoad(fakeDraftId).url

            application.stop()
          }
        }

        "failed matching" must {
          "redirect to FailedMatching" in {

            val answers = emptyUserAnswers
              .set(TrustRegisteredOnlinePage, false).success.value
              .set(TrustHaveAUTRPage, true).success.value
              .set(WhatIsTheUTRPage, utr).success.value
              .set(ExistingTrustMatched, Matched.Failed).success.value

            val application = applicationBuilder(userAnswers = Some(answers)).build()

            val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual routes.FailedMatchController.onPageLoad(fakeDraftId).url

            application.stop()
          }
        }
      }

      "has not attempted matching" must {
        "redirect to WhatIsTrustUTR" in {

          val answers = emptyUserAnswers
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, utr).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.WhatIsTheUTRController.onPageLoad(NormalMode,fakeDraftId).url

          application.stop()
        }
      }
    }

    "a new trust" must {
      "return OK and the correct view for a GET" in {

        forAll(arbitrary[Boolean], arbitrary[Boolean]) {
          (isTaxable, is5mldEnabled) =>

            val answers = emptyUserAnswers
              .set(TrustRegisteredOnlinePage, false).success.value
              .set(TrustHaveAUTRPage, false).success.value
              .set(TrustTaxableYesNoPage, isTaxable).success.value

            when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
              .thenReturn(Future.successful(is5mldEnabled))

            val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = Organisation).build()

            val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            status(result) mustEqual OK

            val view = application.injector.instanceOf[TaskListView]

            contentAsString(result) mustEqual
              view(
                isTaxable = isTaxable,
                draftId = fakeDraftId,
                savedUntil = savedUntil,
                sections = fakeItems,
                additionalSections = fakeAdditionalItems,
                isTaskListComplete = true,
                affinityGroup = Organisation,
                is5mldEnabled = is5mldEnabled
              )(request, messages).toString

            application.stop()
        }
      }
    }
  }
}
