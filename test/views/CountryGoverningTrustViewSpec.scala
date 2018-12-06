package views

import controllers.routes
import forms.CountryGoverningTrustFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.CountryGoverningTrustView

class CountryGoverningTrustViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "countryGoverningTrust"

  val form = new CountryGoverningTrustFormProvider()()

  "CountryGoverningTrustView view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[CountryGoverningTrustView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.CountryGoverningTrustController.onSubmit(NormalMode).url)
  }
}
