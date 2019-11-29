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
import forms.DeclarationFormProvider
import models.core.pages.{Declaration, FullName}
import models.playback
import models.playback.pages.DeclarationWhatNext.DeclareTheTrustIsUpToDate
import org.mockito.Mockito.reset
import pages.{DeclarationPage, DeclarationWhatNextPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup

class DeclarationControllerSpec extends SpecBase {

  val formProvider = new DeclarationFormProvider()
  val form = formProvider()
  val name = "name"

  lazy val declarationRoute: String = controllers.playback.routes.DeclarationController.onPageLoad().url
  lazy val submitRoute: Call = controllers.playback.routes.DeclarationController.onSubmit()

  before {
    reset(mockSubmissionService)
  }

  "DeclarationNoChanges Controller" must {

    "return OK and the correct view for a GET for Organisation user" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Organisation).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[views.html.playback.DeclarationView]
      
      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, AffinityGroup.Organisation, submitRoute)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET for Agent" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[views.html.playback.DeclarationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, AffinityGroup.Agent, submitRoute)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value
        .set(DeclarationPage, Declaration(FullName("First", None, "Last"), Some("test@test.comn"))).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val view = application.injector.instanceOf[views.html.playback.DeclarationView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form.fill(models.core.pages.Declaration(FullName("First",None, "Last"), Some("test@test.comn"))), AffinityGroup.Agent, submitRoute)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the confirmation page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("firstName", "value 1"), ("lastName", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.VariationsConfirmationController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[views.html.playback.DeclarationView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, AffinityGroup.Agent, submitRoute)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
