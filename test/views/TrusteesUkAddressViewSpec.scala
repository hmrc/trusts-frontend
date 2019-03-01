package views

import controllers.routes
import forms.TrusteesUkAddressFormProvider
import models.{NormalMode, TrusteesUkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.TrusteesUkAddressView

class TrusteesUkAddressViewSpec extends QuestionViewBehaviours[TrusteesUkAddress] {

  val messageKeyPrefix = "trusteesUkAddress"

  override val form = new TrusteesUkAddressFormProvider()()

  "TrusteesUkAddressView" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrusteesUkAddressView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.TrusteesUkAddressController.onSubmit(NormalMode).url,
      "field1", "field2"
    )
  }
}
