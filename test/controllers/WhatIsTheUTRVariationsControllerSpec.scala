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
import connector.{TrustClaim, TrustsStoreConnector}
import forms.WhatIsTheUTRFormProvider
import models.NormalMode
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.WhatIsTheUTRVariationPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.WhatIsTheUTRView

import scala.concurrent.Future

class WhatIsTheUTRVariationsControllerSpec extends SpecBase {

  val formProvider = new WhatIsTheUTRFormProvider()
  val form = formProvider()

  lazy val trustUTRRoute = routes.WhatIsTheUTRVariationsController.onPageLoad(NormalMode, fakeDraftId).url

  lazy val onSubmit = routes.WhatIsTheUTRVariationsController.onSubmit(NormalMode, fakeDraftId)

  lazy val connector: TrustsStoreConnector = mock[TrustsStoreConnector]

  "TrustUTR Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trustUTRRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatIsTheUTRView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, onSubmit)(fakeRequest, messages).toString

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
        view(form.fill("0987654321"), NormalMode, fakeDraftId, onSubmit)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to claim a trust microservice when valid UTR is submitted which does not match the locked UTR" in {

      val jsonWithErrorKey = Json.parse(
        """
          |{
          | "trustLocked": false,
          | "managedByAgent": true
          |}
          |""".stripMargin
      )

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustsStoreConnector].toInstance(connector))
          .build()

      when(connector.get(any[String], any[String])(any(), any()))
        .thenReturn(Future.successful(jsonWithErrorKey.asOpt[TrustClaim]))

      val request =
        FakeRequest(POST, trustUTRRoute)
          .withFormUrlEncodedBody(("value", "0987654321"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/trusts-registration/id/status"

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
        view(boundForm, NormalMode, fakeDraftId, onSubmit)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trustUTRRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trustUTRRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
