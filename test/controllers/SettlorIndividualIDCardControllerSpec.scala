package controllers

import base.SpecBase
import forms.SettlorIndividualIDCardFormProvider
import models.{NormalMode, SettlorIndividualIDCard, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.SettlorIndividualIDCardPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.SettlorIndividualIDCardView
import utils._

class SettlorIndividualIDCardControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new SettlorIndividualIDCardFormProvider()
  val form = formProvider()

  lazy val settlorIndividualIDCardRoute = routes.SettlorIndividualIDCardController.onPageLoad(NormalMode, fakeDraftId).url

  val userAnswers = UserAnswers(
    draftId = userAnswersId,
    data = Json.obj(
      SettlorIndividualIDCardPage.toString -> Json.obj(
        "field1" -> "value 1",
        "field2" -> "value 2"
      )
    ),
    internalAuthId = TestUserAnswers.userInternalId
  )

  "SettlorIndividualIDCard Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualIDCardRoute)

      val view = application.injector.instanceOf[SettlorIndividualIDCardView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualIDCardRoute)

      val view = application.injector.instanceOf[SettlorIndividualIDCardView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(SettlorIndividualIDCard("value 1", "value 2")), NormalMode, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, settlorIndividualIDCardRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, settlorIndividualIDCardRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorIndividualIDCardView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorIndividualIDCardRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorIndividualIDCardRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
