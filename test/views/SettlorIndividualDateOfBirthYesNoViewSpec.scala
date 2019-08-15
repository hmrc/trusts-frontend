package views

import controllers.routes
import forms.SettlorIndividualDateOfBirthYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.SettlorIndividualDateOfBirthYesNoView

class SettlorIndividualDateOfBirthYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorIndividualDateOfBirthYesNo"

  val form = new SettlorIndividualDateOfBirthYesNoFormProvider()()

  "SettlorIndividualDateOfBirthYesNo view" must {

    val view = viewFor[SettlorIndividualDateOfBirthYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.SettlorIndividualDateOfBirthYesNoController.onSubmit(NormalMode, fakeDraftId).url)
  }
}
