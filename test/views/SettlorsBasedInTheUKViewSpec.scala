package views

import controllers.routes
import forms.SettlorsBasedInTheUKFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.SettlorsBasedInTheUKView

class SettlorsBasedInTheUKViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorsBasedInTheUK"

  val form = new SettlorsBasedInTheUKFormProvider()()

  "SettlorsBasedInTheUK view" must {

    val view = viewFor[SettlorsBasedInTheUKView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix)
  }
}
