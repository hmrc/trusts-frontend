package views

import controllers.routes
import forms.RegisteringTrustFor5AFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.RegisteringTrustFor5AView

class RegisteringTrustFor5AViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "registeringTrustFor5A"

  val form = new RegisteringTrustFor5AFormProvider()()

  "RegisteringTrustFor5A view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[RegisteringTrustFor5AView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.RegisteringTrustFor5AController.onSubmit(NormalMode).url)
  }
}
