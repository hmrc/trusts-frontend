package views

import views.behaviours.ViewBehaviours
import views.html.FailedMatchView

class MyNewPageViewSpec extends ViewBehaviours {

  "MyNewPage view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[FailedMatchView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "myNewPage")

    behave like pageWithBackLink(applyView)
  }
}
