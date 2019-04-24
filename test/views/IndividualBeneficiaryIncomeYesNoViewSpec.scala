package views

import controllers.routes
import forms.IndividualBeneficiaryIncomeYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IndividualBeneficiaryIncomeYesNoView

class IndividualBeneficiaryIncomeYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryIncomeYesNo"

  val form = new IndividualBeneficiaryIncomeYesNoFormProvider()()

  "IndividualBeneficiaryIncomeYesNo view" must {

    val view = viewFor[IndividualBeneficiaryIncomeYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IndividualBeneficiaryIncomeYesNoController.onSubmit(NormalMode).url)
  }
}
