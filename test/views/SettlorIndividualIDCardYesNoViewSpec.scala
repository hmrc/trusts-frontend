package views

import controllers.routes
import forms.SettlorIndividualIDCardYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.SettlorIndividualIDCardYesNoView

class SettlorIndividualIDCardYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorIndividualIDCardYesNo"

  val form = new SettlorIndividualIDCardYesNoFormProvider()()

  "SettlorIndividualIDCardYesNo view" must {

    val view = viewFor[SettlorIndividualIDCardYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.SettlorIndividualIDCardYesNoController.onSubmit(NormalMode, fakeDraftId).url)
  }
}
