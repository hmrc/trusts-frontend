package views

import controllers.routes
import forms.TrustResidentOffshoreFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.TrustResidentOffshoreView

class TrustResidentOffshoreViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trustResidentOffshore"

  val form = new TrustResidentOffshoreFormProvider()()

  "TrustResidentOffshore view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrustResidentOffshoreView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.TrustResidentOffshoreController.onSubmit(NormalMode).url)
  }
}
