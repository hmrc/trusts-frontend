package views

import controllers.routes
import forms.GovernedOutsideTheUKFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.GovernedOutsideTheUKView

class GovernedOutsideTheUKViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "governedOutsideTheUK"

  val form = new GovernedOutsideTheUKFormProvider()()

  "GovernedOutsideTheUK view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[GovernedOutsideTheUKView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.GovernedOutsideTheUKController.onSubmit(NormalMode).url)
  }
}
