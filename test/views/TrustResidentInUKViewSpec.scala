package views

import controllers.routes
import forms.TrustResidentInUKFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.TrustResidentInUKView

class TrustResidentInUKViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trustResidentInUK"

  val form = new TrustResidentInUKFormProvider()()

  "TrustResidentInUK view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrustResidentInUKView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.TrustResidentInUKController.onSubmit(NormalMode).url)
  }
}
