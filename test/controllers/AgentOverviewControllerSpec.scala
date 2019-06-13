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

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.AgentOverviewView
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import viewmodels.DraftRegistration

import scala.concurrent.Future

class AgentOverviewControllerSpec extends SpecBase {

  "AgentOverview Controller" when {

    "there are no drafts" must {

      "return OK and the correct view for a GET" in {

        when(mockedSessionRepository.listDrafts(any())).thenReturn(Future.successful(Nil))

        val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

        val request = FakeRequest(GET, routes.AgentOverviewController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AgentOverviewView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(Nil)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "there are drafts" must {

      "return OK and the correct view for a GET" in {

        val draft = List(DraftRegistration("draftId", "InternalRef", LocalDateTime.now()))

        when(mockedSessionRepository.listDrafts(any()))
          .thenReturn(Future.successful(draft))

        val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

        val request = FakeRequest(GET, routes.AgentOverviewController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AgentOverviewView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(draft)(fakeRequest, messages).toString

        application.stop()
      }
    }
  }
}
