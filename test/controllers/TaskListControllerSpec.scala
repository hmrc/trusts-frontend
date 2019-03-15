/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import base.SpecBase
import models.{NormalMode, UserAnswers}
import pages.{ExistingTrustMatched, TrustHaveAUTRPage, TrustRegisteredOnlinePage, WhatIsTheUTRPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.Task
import views.html.TaskListView

class TaskListControllerSpec extends SpecBase {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val savedUntil : String = LocalDateTime.now.plusSeconds(frontendAppConfig.ttlInSeconds).format(dateFormatter)

  val sections = List(
    Task("trust-details", routes.AddATrusteeController.onPageLoad(), None),
    Task("settlors", routes.AddATrusteeController.onPageLoad(), None),
    Task("trustees", routes.AddATrusteeController.onPageLoad(), None),
    Task("beneficiaries", routes.AddATrusteeController.onPageLoad(), None),
    Task("assets", routes.AddATrusteeController.onPageLoad(), None),
    Task("tax-liability", routes.AddATrusteeController.onPageLoad(), None)
  )

  "TaskList Controller" must {

    "redirect to RegisteredOnline when no required answer" in {

      val answers = UserAnswers(userAnswersId)

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrustRegisteredOnlineController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to TrustHaveAUTR when no required answer" in {

      val answers = UserAnswers(userAnswersId).set(TrustRegisteredOnlinePage, true).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrustHaveAUTRController.onPageLoad(NormalMode).url

      application.stop()
    }

    "for an existing trust" when {

      "has matched" when {

        "return OK and the correct view for a GET" in {

          val answers = UserAnswers(userAnswersId)
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "SA123456789").success.value
            .set(ExistingTrustMatched, true).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[TaskListView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(savedUntil, sections)(fakeRequest, messages).toString

          application.stop()
        }

      }

      "has not matched" when {

        "redirect to FailedMatching" in {

          val answers = UserAnswers(userAnswersId)
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "SA123456789").success.value
            .set(ExistingTrustMatched, false).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.FailedMatchController.onPageLoad().url

          application.stop()
        }

      }

      "has not attempted matching" when {

        "redirect to WhatIsTrustUTR" in {
          val answers = UserAnswers(userAnswersId)
            .set(TrustRegisteredOnlinePage, false).success.value
            .set(TrustHaveAUTRPage, true).success.value
            .set(WhatIsTheUTRPage, "SA123456789").success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.TaskListController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.WhatIsTheUTRController.onPageLoad(NormalMode).url

          application.stop()
        }

      }

    }

    "for a new trust" which {

      "return OK and the correct view for a GET" in {

        val answers = UserAnswers(userAnswersId)
          .set(TrustRegisteredOnlinePage, false).success.value
          .set(TrustHaveAUTRPage, false).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val request = FakeRequest(GET, routes.TaskListController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(savedUntil, sections)(fakeRequest, messages).toString

        application.stop()
      }

    }


  }
}
