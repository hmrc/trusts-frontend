package views

import controllers.routes
import forms.SettlorIndividualAddressUKYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.SettlorIndividualAddressUKYesNoView

class SettlorIndividualAddressUKYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorIndividualAddressUKYesNo"

  val form = new SettlorIndividualAddressUKYesNoFormProvider()()

  "SettlorIndividualAddressUKYesNo view" must {

    val view = viewFor[SettlorIndividualAddressUKYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.SettlorIndividualAddressUKYesNoController.onSubmit(NormalMode, fakeDraftId).url)
  }
}
