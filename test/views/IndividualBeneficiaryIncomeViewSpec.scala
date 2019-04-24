package views

import controllers.routes
import forms.IndividualBeneficiaryIncomeFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.IndividualBeneficiaryIncomeView

class IndividualBeneficiaryIncomeViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryIncome"

  val form = new IndividualBeneficiaryIncomeFormProvider()()

  "IndividualBeneficiaryIncomeView view" must {

    val view = viewFor[IndividualBeneficiaryIncomeView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.IndividualBeneficiaryIncomeController.onSubmit(NormalMode).url)
  }
}
