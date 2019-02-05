package views

import views.behaviours.ViewBehaviours
import views.html.TrusteesAnswerPageView

class TrusteesAnswerPageViewSpec extends ViewBehaviours {

  "TrusteesAnswerPage view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrusteesAnswerPageView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "trusteesAnswerPage")

    behave like pageWithBackLink(applyView)
  }
}
