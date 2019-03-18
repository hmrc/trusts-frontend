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

import base.SpecBase
import models.NormalMode
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup

class IndexControllerSpec extends SpecBase {

  "Index Controller" must {

    "redirect to TrustRegisteredOnline with Non-Agent affinityGroup for a GET" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.TrustRegisteredOnlineController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to AgentOverview with Agent affinityGroup for a GET" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.AgentOverviewController.onPageLoad().url

      application.stop()
    }


  }

}
