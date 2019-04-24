package views

import controllers.routes
import forms.IndividualBeneficiaryNationalInsuranceNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.IndividualBeneficiaryNationalInsuranceNumberView

class IndividualBeneficiaryNationalInsuranceNumberViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryNationalInsuranceNumber"

  val form = new IndividualBeneficiaryNationalInsuranceNumberFormProvider()()

  "IndividualBeneficiaryNationalInsuranceNumberView view" must {

    val view = viewFor[IndividualBeneficiaryNationalInsuranceNumberView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.IndividualBeneficiaryNationalInsuranceNumberController.onSubmit(NormalMode).url)
  }
}
