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

package controllers.register.trustees

import base.RegistrationSpecBase
import controllers.register.routes._
import forms.RemoveIndexFormProvider
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages.register.trustees.TrusteeOrgNamePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.RemoveIndexView

class RemoveTrusteeOrgControllerSpec extends RegistrationSpecBase with PropertyChecks {

  val messagesPrefix = "removeATrusteeOrg"

  lazy val formProvider = new RemoveIndexFormProvider()
  lazy val form = formProvider(messagesPrefix)

  lazy val formRoute = routes.RemoveTrusteeOrgController.onSubmit(0, fakeDraftId)

  lazy val content : String = "John Smith Org"
  lazy val defaultContent : String = "the trustee"

  val index = 0

  "TrusteeOrgRemove Controller" when {

    "no name provided" must {
      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.RemoveTrusteeOrgController.onPageLoad(index, fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(messagesPrefix, form, index, fakeDraftId, defaultContent, formRoute)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "name has been provided" must {
      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrgNamePage(0), content).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.RemoveTrusteeOrgController.onPageLoad(index, fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(messagesPrefix, form, index, fakeDraftId, content, formRoute)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(0), content).success.value

      forAll(arbitrary[Boolean]) {
        value =>
          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .build()

          val request =
            FakeRequest(POST, routes.RemoveTrusteeOrgController.onSubmit(index, fakeDraftId).url)
              .withFormUrlEncodedBody(("value", value.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.AddATrusteeController.onPageLoad(fakeDraftId).url

          application.stop()
      }

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(0), content).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, routes.RemoveTrusteeOrgController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveIndexView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(messagesPrefix, boundForm, index, fakeDraftId, content, formRoute)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.RemoveTrusteeOrgController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, routes.RemoveTrusteeOrgController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
