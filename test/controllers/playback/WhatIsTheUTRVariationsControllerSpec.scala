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
import forms.WhatIsTheUTRFormProvider
import pages.playback.WhatIsTheUTRVariationPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import views.html.register.WhatIsTheUTRView

class WhatIsTheUTRVariationsControllerSpec extends SpecBase {

  val formProvider = new WhatIsTheUTRFormProvider()
  val form = formProvider()

  lazy val trustUTRRoute = routes.WhatIsTheUTRVariationsController.onPageLoad().url

  lazy val onSubmit = routes.WhatIsTheUTRVariationsController.onSubmit()

  "TrustUTR Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trustUTRRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatIsTheUTRView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, onSubmit)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, "0987654321").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trustUTRRoute)

      val view = application.injector.instanceOf[WhatIsTheUTRView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("0987654321"), onSubmit)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to trust status on a POST" in {

      val utr = "0987654321"

      val enrolments = Enrolments(Set(Enrolment(
        "HMRC-TERS-ORG", Seq(EnrolmentIdentifier("SAUTR", utr)), "Activated"
      )))

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers),
          affinityGroup = Organisation,
          enrolments = enrolments
        ).build()

      implicit val request = FakeRequest(POST, trustUTRRoute).withFormUrlEncodedBody(("value", utr))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.TrustStatusController.status().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, trustUTRRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[WhatIsTheUTRView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, onSubmit)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trustUTRRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trustUTRRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
