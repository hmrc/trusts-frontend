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

package controllers.property_or_land

import base.SpecBase
import forms.property_or_land.PropertyLandValueTrustFormProvider
import models.{NormalMode, PropertyLandValueTrust, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.PropertyLandValueTrustPage
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils._
import views.html.property_or_land.PropertyLandValueTrustView

class PropertyLandValueTrustControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PropertyLandValueTrustFormProvider()
  val form: Form[PropertyLandValueTrust] = formProvider()

  val index: Int = 0

  lazy val propertyLandValueTrustRoute = routes.PropertyLandValueTrustController.onPageLoad(NormalMode, index, fakeDraftId).url

  val userAnswers = UserAnswers(
    draftId = userAnswersId,
    data = Json.obj(
      PropertyLandValueTrustPage.toString -> Json.obj(
        "field1" -> "value 1"
      )
    ),
    internalAuthId = TestUserAnswers.userInternalId
  )

  "PropertyLandValueTrust Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, propertyLandValueTrustRoute)

      val view = application.injector.instanceOf[PropertyLandValueTrustView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, propertyLandValueTrustRoute)

      val view = application.injector.instanceOf[PropertyLandValueTrustView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(PropertyLandValueTrust("value 1")), NormalMode, index, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, propertyLandValueTrustRoute)
          .withFormUrlEncodedBody(("field1", "100"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, propertyLandValueTrustRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[PropertyLandValueTrustView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, index, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, propertyLandValueTrustRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, propertyLandValueTrustRoute)
          .withFormUrlEncodedBody(("field1", "value 1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
