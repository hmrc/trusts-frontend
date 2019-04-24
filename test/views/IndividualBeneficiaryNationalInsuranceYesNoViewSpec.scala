package views

import controllers.routes
import forms.IndividualBeneficiaryNationalInsuranceYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IndividualBeneficiaryNationalInsuranceYesNoView

class IndividualBeneficiaryNationalInsuranceYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryNationalInsuranceYesNo"

  val form = new IndividualBeneficiaryNationalInsuranceYesNoFormProvider()()

  "IndividualBeneficiaryNationalInsuranceYesNo view" must {

    val view = viewFor[IndividualBeneficiaryNationalInsuranceYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IndividualBeneficiaryNationalInsuranceYesNoController.onSubmit(NormalMode).url)
  }
}
