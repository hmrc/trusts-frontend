package views

import controllers.routes
import forms.IndividualBeneficiaryIsAddressUKFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IndividualBeneficiaryIsAddressUKView

class IndividualBeneficiaryIsAddressUKViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryIsAddressUK"

  val form = new IndividualBeneficiaryIsAddressUKFormProvider()()

  "IndividualBeneficiaryIsAddressUK view" must {

    val view = viewFor[IndividualBeneficiaryIsAddressUKView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IndividualBeneficiaryIsAddressUKController.onSubmit(NormalMode).url)
  }
}
