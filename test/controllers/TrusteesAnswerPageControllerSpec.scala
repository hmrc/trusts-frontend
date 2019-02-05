package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TrusteesAnswerPageView

class TrusteesAnswerPageControllerSpec extends SpecBase {

  "TrusteesAnswerPage Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteesAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(fakeRequest, messages).toString

      application.stop()
    }
  }
}
