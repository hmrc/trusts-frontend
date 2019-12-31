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

import base.PlaybackSpecBase
import pages.playback.WhatIsTheUTRVariationPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.playback.TrustNotClaimedView

class TrustNotClaimedControllerSpec extends PlaybackSpecBase {

  "TrustNotClaimed Controller" must {

    "return OK and the correct view for a GET" in {

      val utr = "0987654321"

      val answers = emptyUserAnswers
        .set(WhatIsTheUTRVariationPage, utr)
        .success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TrustNotClaimedController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrustNotClaimedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }
  }
}
