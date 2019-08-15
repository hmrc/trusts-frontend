package views

import controllers.routes
import forms.SettlorIndividualAddressYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.SettlorIndividualAddressYesNoView

class SettlorIndividualAddressYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorIndividualAddressYesNo"

  val form = new SettlorIndividualAddressYesNoFormProvider()()

  "SettlorIndividualAddressYesNo view" must {

    val view = viewFor[SettlorIndividualAddressYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.SettlorIndividualAddressYesNoController.onSubmit(NormalMode, fakeDraftId).url)
  }
}
