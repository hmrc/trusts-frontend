package views

import views.behaviours.ViewBehaviours
import views.html.AssetInterruptPageView

class AssetInterruptPageViewSpec extends ViewBehaviours {

  "AssetInterruptPage view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[AssetInterruptPageView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "assetInterruptPage")

    behave like pageWithBackLink(applyView)
  }
}
