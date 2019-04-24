package views

import controllers.routes
import forms.IndividualBeneficiaryAdressYesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IndividualBeneficiaryAdressYesNoView

class IndividualBeneficiaryAdressYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryAdressYesNo"

  val form = new IndividualBeneficiaryAdressYesNoFormProvider()()

  "IndividualBeneficiaryAdressYesNo view" must {

    val view = viewFor[IndividualBeneficiaryAdressYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IndividualBeneficiaryAdressYesNoController.onSubmit(NormalMode).url)
  }
}
