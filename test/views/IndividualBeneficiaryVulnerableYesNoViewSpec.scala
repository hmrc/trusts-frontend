package views

import controllers.routes
import forms.IndividualBeneficiaryVulnerableYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IndividualBeneficiaryVulnerableYesNoView

class IndividualBeneficiaryVulnerableYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryVulnerableYesNo"

  val form = new IndividualBeneficiaryVulnerableYesNoFormProvider()()

  "IndividualBeneficiaryVulnerableYesNo view" must {

    val view = viewFor[IndividualBeneficiaryVulnerableYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IndividualBeneficiaryVulnerableYesNoController.onSubmit(NormalMode).url)
  }
}
