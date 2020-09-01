package views

class $className$ViewSpec extends ViewBehaviours {

  "$className$ view" must {

    val view = viewFor[$className$View](Some(emptyUserAnswers))

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, None, "$className;format="decap"$")

    behave like pageWithBackLink(applyView)
  }
}
