package views

import controllers.routes
import forms.TrustNameFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.TrustNameView

class TrustNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trustName"

  val form = new TrustNameFormProvider()()

  "TrustNameView view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrustNameView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.TrustNameController.onSubmit(NormalMode).url)
  }
}
