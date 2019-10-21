package controllers

import base.SpecBase
import play.api.test.FakeRequest
import views.html.CannotAccessErrorView
import play.api.test.Helpers._

class TrustStatusControllerSpec extends SpecBase{

  "TrustStatus Controller" when {

    "navigating to GET ../" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.TrustStatusController.cannotAccess().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CannotAccessErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(fakeRequest, messages).toString

      application.stop()
    }
  }
}
