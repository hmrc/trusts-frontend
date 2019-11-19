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
import forms.DeclarationChangesNoChangesFormProvider
import models.{DeclarationChangesNoChanges, FullName, NormalMode}
import org.mockito.Mockito._
import pages.{DeclarationChangesNoChangesPage, DeclarationWhatNextPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.DeclarationChangesNoChangesView
import models.DeclarationWhatNext.DeclareTheTrustIsUpToDate

class DeclarationNoChangesControllerSpec extends SpecBase {

  def confirmationRoute = Call("GET", "/confirmation")

  val formProvider = new DeclarationChangesNoChangesFormProvider()
  val form = formProvider()
  val name = "name"

  lazy val declarationRoute = routes.DeclarationNoChangesController.onPageLoad().url
  lazy val submitRoute = routes.DeclarationNoChangesController.onSubmit()

  before {
    reset(mockSubmissionService)
  }

  "DeclarationNoChanges Controller" must {

    "return OK and the correct view for a GET for Organisation user" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Organisation).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeclarationChangesNoChangesView]

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

      val view = application.injector.instanceOf[DeclarationChangesNoChangesView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, AffinityGroup.Agent, submitRoute)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value
        .set(DeclarationChangesNoChangesPage, DeclarationChangesNoChanges(FullName("First", None, "Last"), Some("test@test.comn"))).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val view = application.injector.instanceOf[DeclarationChangesNoChangesView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(DeclarationChangesNoChanges(FullName("First",None, "Last"), Some("test@test.comn"))), AffinityGroup.Agent, submitRoute)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the confirmation page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("firstName", "value 1"), ("lastName", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.DeclarationNoChangesController.onPageLoad().url // TODO Redirect to variation confirmation page

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclareTheTrustIsUpToDate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DeclarationChangesNoChangesView]

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

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
