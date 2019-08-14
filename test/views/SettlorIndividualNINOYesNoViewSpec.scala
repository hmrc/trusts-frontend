package views

import controllers.routes
import forms.SettlorIndividualNINOYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.SettlorIndividualNINOYesNoView

class SettlorIndividualNINOYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorIndividualNINOYesNo"

  val form = new SettlorIndividualNINOYesNoFormProvider()()

  "SettlorIndividualNINOYesNo view" must {

    val view = viewFor[SettlorIndividualNINOYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.SettlorIndividualNINOYesNoController.onSubmit(NormalMode, fakeDraftId).url)
  }
}
