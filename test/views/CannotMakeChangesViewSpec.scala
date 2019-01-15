package views

import views.behaviours.ViewBehaviours
import views.html.CannotMakeChangesView

class CannotMakeChangesViewSpec extends ViewBehaviours {

  "CannotMakeChanges view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[CannotMakeChangesView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "cannotMakeChanges")

    behave like pageWithBackLink(applyView)
  }
}
