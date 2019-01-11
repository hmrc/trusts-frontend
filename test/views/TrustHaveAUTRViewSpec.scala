package views

import controllers.routes
import forms.TrustHaveAUTRFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.TrustHaveAUTRView

class TrustHaveAUTRViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trustHaveAUTR"

  val form = new TrustHaveAUTRFormProvider()()

  "TrustHaveAUTR view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrustHaveAUTRView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.TrustHaveAUTRController.onSubmit(NormalMode).url)
  }
}
