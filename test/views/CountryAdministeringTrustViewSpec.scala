package views

import controllers.routes
import forms.CountryAdministeringTrustFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.CountryAdministeringTrustView

class CountryAdministeringTrustViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "countryAdministeringTrust"

  val form = new CountryAdministeringTrustFormProvider()()

  "CountryAdministeringTrustView view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[CountryAdministeringTrustView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.CountryAdministeringTrustController.onSubmit(NormalMode).url)
  }
}
