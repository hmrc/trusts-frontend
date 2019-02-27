package views

import controllers.routes
import forms.TrusteeLiveInTheUKFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.TrusteeLiveInTheUKView

class TrusteeLiveInTheUKViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trusteeLiveInTheUK"

  val form = new TrusteeLiveInTheUKFormProvider()()

  "TrusteeLiveInTheUK view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrusteeLiveInTheUKView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.TrusteeLiveInTheUKController.onSubmit(NormalMode).url)
  }
}
