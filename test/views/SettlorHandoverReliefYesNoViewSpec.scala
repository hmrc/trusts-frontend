package views

import controllers.routes
import forms.SettlorHandoverReliefYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.SettlorHandoverReliefYesNoView

class SettlorHandoverReliefYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorHandoverReliefYesNo"

  val form = new SettlorHandoverReliefYesNoFormProvider()()

  "SettlorHandoverReliefYesNo view" must {

    val view = viewFor[SettlorHandoverReliefYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix)
  }
}
