/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.register.suitability

import base.RegistrationSpecBase
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.TrustHaveAUTRPage
import pages.register.suitability.TrustTaxableYesNoPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.register.suitability._

class BeforeYouContinueControllerSpec extends RegistrationSpecBase with ScalaCheckPropertyChecks {

  private lazy val beforeYouContinueRoute: String = routes.BeforeYouContinueController.onPageLoad().url

  "BeforeYouContinue Controller" must {

    "return OK and the correct view for a taxable journey GET" in {

      val answers = emptyMatchingAndSuitabilityUserAnswers
        .set(TrustTaxableYesNoPage, true)
        .success
        .value
        .set(TrustHaveAUTRPage, false)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers))
        .build()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BeforeYouContinueTaxableView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a taxable agent journey GET" in {

      val answers = emptyMatchingAndSuitabilityUserAnswers
        .set(TrustTaxableYesNoPage, true)
        .success
        .value
        .set(TrustHaveAUTRPage, false)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Agent)
        .build()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BeforeYouContinueTaxableAgentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for an existing taxable journey GET" in {

      val answers = emptyMatchingAndSuitabilityUserAnswers
        .set(TrustTaxableYesNoPage, true)
        .success
        .value
        .set(TrustHaveAUTRPage, true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers))
        .build()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BeforeYouContinueExistingTaxableView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a non taxable journey GET" in {

      val answers = emptyMatchingAndSuitabilityUserAnswers
        .set(TrustTaxableYesNoPage, false)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Organisation)
        .build()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BeforeYouContinueNonTaxableView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a non taxable agent journey GET" in {

      val answers = emptyMatchingAndSuitabilityUserAnswers
        .set(TrustTaxableYesNoPage, false)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Agent)
        .build()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BeforeYouContinueNonTaxAgentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" must {

      "create the draft registration" in {
        val answers = emptyMatchingAndSuitabilityUserAnswers

        val application =
          applicationBuilder(userAnswers = Some(answers), affinityGroup = AffinityGroup.Organisation).build()

        val request = FakeRequest(POST, beforeYouContinueRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.routes.CreateDraftRegistrationController
          .create()
          .url

        application.stop()
      }
    }
  }

}
