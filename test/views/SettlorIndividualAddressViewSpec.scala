package views

import controllers.routes
import models.{NormalMode, SettlorIndividualAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorIndividualAddressView

class SettlorIndividualAddressViewSpec extends QuestionViewBehaviours[SettlorIndividualAddress] {

  val messageKeyPrefix = "settlorIndividualAddress"

  override val form = new SettlorIndividualAddressFormProvider()()

  "SettlorIndividualAddressView" must {

    val view = viewFor[SettlorIndividualAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.SettlorIndividualAddressController.onSubmit(NormalMode, fakeDraftId).url,
      Seq(("field1", None), ("field2", None))
    )
  }
}
