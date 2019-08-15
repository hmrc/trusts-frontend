package views

import controllers.routes
import forms.SettlorIndividualNINOFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.SettlorIndividualNINOView

class SettlorIndividualNINOViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "settlorIndividualNINO"

  val form = new SettlorIndividualNINOFormProvider()()

  "SettlorIndividualNINOView view" must {

    val view = viewFor[SettlorIndividualNINOView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.SettlorIndividualNINOController.onSubmit(NormalMode, fakeDraftId).url)
  }
}
