package views

import controllers.routes
import forms.TrustPreviouslyResidentFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.TrustPreviouslyResidentView

class TrustPreviouslyResidentViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trustPreviouslyResident"

  val form = new TrustPreviouslyResidentFormProvider()()

  "TrustPreviouslyResidentView view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrustPreviouslyResidentView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.TrustPreviouslyResidentController.onSubmit(NormalMode).url)
  }
}
