package views

import controllers.routes
import forms.SettlorIndividualPassportYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.SettlorIndividualPassportYesNoView

class SettlorIndividualPassportYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorIndividualPassportYesNo"

  val form = new SettlorIndividualPassportYesNoFormProvider()()

  "SettlorIndividualPassportYesNo view" must {

    val view = viewFor[SettlorIndividualPassportYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.SettlorIndividualPassportYesNoController.onSubmit(NormalMode, fakeDraftId).url)
  }
}
