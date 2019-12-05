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

package controllers.playback

import base.SpecBase
import pages.playback.WhatIsTheUTRVariationPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import views.html.playback.InformationMaintainingThisTrustView

class InformationMaintainingThisTrustControllerSpec extends SpecBase {

  "InformationMaintainingThisTrustPage Controller" must {

    val utr = "1234545678"

    "return OK and the correct view for a GET" when {
      "variations is switched off" when {
        "agent" in {

          val userAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, utr).success.value

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .configure("microservice.services.features.playback.enabled" -> false)
            .build()

          val request = FakeRequest(GET, routes.InformationMaintainingThisTrustController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[AgentCannotAccessTrustYetView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(utr)(fakeRequest, messages).toString

          application.stop()
        }

        "organisation" in {

          val userAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, utr).success.value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .configure("microservice.services.features.playback.enabled" -> true)
            .build()

          val request = FakeRequest(GET, routes.InformationMaintainingThisTrustController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[InformationMaintainingThisTrustView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(utr)(fakeRequest, messages).toString

          application.stop()
        }
      }
    }

    "redirect to WhatIsTheUTRPage when WhatIsTheUTRPage is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.InformationMaintainingThisTrustController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation (result).value mustEqual routes.WhatIsTheUTRVariationsController.onPageLoad().url

      application.stop()
    }
  }
}
