package views

import controllers.routes
import forms.TelephoneNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.TelephoneNumberView

class TelephoneNumberViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "telephoneNumber"

  val form = new TelephoneNumberFormProvider()()

  "TelephoneNumberView view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TelephoneNumberView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.TelephoneNumberController.onSubmit(NormalMode).url)
  }
}
