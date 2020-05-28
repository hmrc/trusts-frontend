/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import base.RegistrationSpecBase
import models.NormalMode
import models.core.UserAnswers
import models.registration.Matched
import navigation.Navigator
import navigation.registration.TaskListNavigator
import pages.register._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import views.html.register.TaskListView

class TaskListControllerSpec extends RegistrationSpecBase {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.plusSeconds(frontendAppConfig.ttlInSeconds).format(dateFormatter)

  private def sections(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).items(answers, fakeDraftId)

  private def isTaskListComplete(answers: UserAnswers) =
    new RegistrationProgress(new TaskListNavigator()).isTaskListComplete(answers)

  override protected def applicationBuilder(
                                             userAnswers: Option[UserAnswers],
                                             affinityGroup: AffinityGroup,
                                             enrolments: Enrolments = Enrolments(Set.empty[Enrolment]),
                                             navigator: Navigator = fakeNavigator
                                           ): GuiceApplicationBuilder =
    super.applicationBuilder(userAnswers, affinityGroup).configure(("microservice.services.features.removeTaxLiabilityOnTaskList", false))

  "TaskList Controller" must {

    "redirect to RegisteredOnline when no required answer" in {

      val answers = emptyUserAnswers

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrustRegisteredOnlineController.onPageLoad(NormalMode,fakeDraftId).url

      application.stop()
    }

    "redirect to TrustHaveAUTR when no required answer" in {

      val answers = emptyUserAnswers.set(TrustRegisteredOnlinePage, true).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrustHaveAUTRController.onPageLoad(NormalMode,fakeDraftId).url

      application.stop()
    }

    "for an existing trust" when {

      "has matched" must {

        "return OK and the correct view for a GET" in {

          val answers = emptyUserAnswers
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "SA123456789").success.value
            .set(ExistingTrustMatched, Matched.Success).success.value

          val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = Organisation).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[TaskListView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(fakeDraftId, savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages).toString

          application.stop()
        }

      }

      "has not matched" must {

        "already registered redirect to AlreadyRegistered" in {
          val answers = emptyUserAnswers
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "SA123456789").success.value
            .set(ExistingTrustMatched, Matched.AlreadyRegistered).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.FailedMatchController.onPageLoad(fakeDraftId).url

          application.stop()
        }

        "failed matching redirect to FailedMatching" in {

          val answers = emptyUserAnswers
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "SA123456789").success.value
            .set(ExistingTrustMatched, Matched.Failed).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.FailedMatchController.onPageLoad(fakeDraftId).url

          application.stop()
        }

      }

      "has not attempted matching" must {

        "redirect to WhatIsTrustUTR" in {
          val answers = emptyUserAnswers
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "SA123456789").success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.WhatIsTheUTRController.onPageLoad(NormalMode,fakeDraftId).url

          application.stop()
        }

      }

    }

    "for a new trust" must {

      "return OK and the correct view for a GET" in {

        val answers = emptyUserAnswers
          .set(TrustRegisteredOnlinePage, false).success.value
          .set(TrustHaveAUTRPage, false).success.value

        val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = Organisation).build()

        val request = FakeRequest(GET, routes.TaskListController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, savedUntil, sections(answers), isTaskListComplete(answers), Organisation)(fakeRequest, messages).toString

        application.stop()
      }

    }


  }
}
