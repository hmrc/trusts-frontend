package views

import views.behaviours.ViewBehaviours
import views.html.TrusteesInfoView

class TrusteesInfoViewSpec extends ViewBehaviours {

  "TrusteesInfo view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrusteesInfoView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "trusteesInfo")

    behave like pageWithBackLink(applyView)
  }
}
