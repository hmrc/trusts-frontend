package views

import controllers.routes
import forms.IndividualBeneficiaryAddressUKFormProvider
import models.{NormalMode, IndividualBeneficiaryAddressUK}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.IndividualBeneficiaryAddressUKView

class IndividualBeneficiaryAddressUKViewSpec extends QuestionViewBehaviours[IndividualBeneficiaryAddressUK] {

  val messageKeyPrefix = "individualBeneficiaryAddressUK"

  override val form = new IndividualBeneficiaryAddressUKFormProvider()()

  "IndividualBeneficiaryAddressUKView" must {

    val view = viewFor[IndividualBeneficiaryAddressUKView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.IndividualBeneficiaryAddressUKController.onSubmit(NormalMode).url,
      Seq(("field1", None), ("field2", None))
    )
  }
}
