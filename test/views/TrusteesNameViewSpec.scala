package views

import controllers.routes
import forms.TrusteesNameFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.TrusteesNameView

class TrusteesNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trusteesName"

  val form = new TrusteesNameFormProvider()()

  "TrusteesNameView view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrusteesNameView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.TrusteesNameController.onSubmit(NormalMode).url)
  }
}
