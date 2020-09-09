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

import base.RegistrationSpecBase
import models.NormalMode
import models.core.UserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.verify
import pages.register.{PostcodeForTheTrustPage, TrustRegisteredWithUkAddressYesNoPage, WhatIsTheUTRPage}
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.trust_details.TrustNamePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup._
import views.html.register.TrustAlreadyRegisteredView

class TrustAlreadyRegisteredControllerSpec extends RegistrationSpecBase {

  private lazy val trustAlreadyRegisteredRoute: String = routes.TrustAlreadyRegisteredController.onPageLoad(fakeDraftId).url

  "TrustAlreadyRegistered Controller" must {

    "return OK and the correct view for a GET" when {

      val fakeUtr: String = "utr"
      val userAnswers: UserAnswers = emptyUserAnswers.set(WhatIsTheUTRPage, fakeUtr).success.value

      "agent user" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent).build()

        val request = FakeRequest(GET, trustAlreadyRegisteredRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustAlreadyRegisteredView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, fakeUtr, isAgent = true)(request, messages).toString

        application.stop()
      }

      "non-agent user" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Organisation).build()

        val request = FakeRequest(GET, trustAlreadyRegisteredRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustAlreadyRegisteredView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, fakeUtr, isAgent = false)(request, messages).toString

        application.stop()
      }

    }

    "redirect to WhatIsTheUtrController when no UTR found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trustAlreadyRegisteredRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.WhatIsTheUTRController.onPageLoad(NormalMode, fakeDraftId).url

      application.stop()
    }

    "cleanup pages and redirect to WhatIsTheUtrController for a POST" in {

      val userAnswers = emptyUserAnswers
        .set(WhatIsTheUTRPage, "utr").success.value
        .set(TrustNamePage, "name").success.value
        .set(TrustRegisteredWithUkAddressYesNoPage, true).success.value
        .set(PostcodeForTheTrustPage, "postcode").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, trustAlreadyRegisteredRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.WhatIsTheUTRController.onPageLoad(NormalMode, fakeDraftId).url

      val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(registrationsRepository).set(uaCaptor.capture)(any())
      uaCaptor.getValue.get(WhatIsTheUTRPage) mustNot be(defined)
      uaCaptor.getValue.get(TrustNamePage) mustNot be(defined)
      uaCaptor.getValue.get(TrustRegisteredWithUkAddressYesNoPage) mustNot be(defined)
      uaCaptor.getValue.get(PostcodeForTheTrustPage) mustNot be(defined)

      application.stop()
    }
  }
}
