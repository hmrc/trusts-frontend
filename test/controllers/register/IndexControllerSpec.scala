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

package controllers.register

import base.RegistrationSpecBase
import controllers.register.agents.routes._
import models.registration.pages.RegistrationStatus.InProgress
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import pages.register.TrustRegisteredOnlinePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import org.mockito.Mockito.{reset, verify, when}
import scala.concurrent.Future

class IndexControllerSpec extends RegistrationSpecBase with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    reset(cacheRepository)
    when(cacheRepository.set(any())).thenReturn(Future.successful(true))
  }

  "Index Controller" must {

    "redirect when form has not been started" should {

      "redirect to TrustRegisteredOnlineController with Non-Agent affinityGroup for a GET" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe routes.TrustRegisteredOnlineController.onPageLoad().url

        verify(cacheRepository).set(any())

        application.stop()
      }

      "redirect to AgentOverview with Agent affinityGroup for a GET" in {

        val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

        val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe AgentOverviewController.onPageLoad().url

        application.stop()
      }

    }

    "redirect when registration has been started" when {

      "TrustRegisteredOnlinePage unanswered" should {
        "redirect to TrustRegisteredOnlineController" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.TrustRegisteredOnlineController.onPageLoad().url

          verify(cacheRepository).set(any())

          application.stop()
        }
      }

      "for a playback" should {
        "redirect to maintain a trust with Non-Agent affinityGroup" in {

          val answers = emptyUserAnswers.copy(progress = InProgress)
            .set(TrustRegisteredOnlinePage, true).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value must include("/maintain-a-trust")

          application.stop()
        }
      }

      "for a registration" should {

        "redirect to RegistrationProgress with Non-Agent affinityGroup" in {

          val answers = emptyUserAnswers.copy(progress = InProgress)
            .set(TrustRegisteredOnlinePage, false).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.TaskListController.onPageLoad(fakeDraftId).url

          application.stop()
        }

        "redirect to AgentOverview with Agent affinityGroup" in {

          val answers = emptyUserAnswers.copy(progress = InProgress)
            .set(TrustRegisteredOnlinePage, false).success.value

          val application = applicationBuilder(userAnswers = Some(answers), AffinityGroup.Agent).build()

          val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe AgentOverviewController.onPageLoad().url

          application.stop()
        }

      }
    }
  }
}
