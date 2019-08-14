package views

import controllers.routes
import forms.SettlorIndividualAddressUKFormProvider
import models.{NormalMode, SettlorIndividualAddressUK}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorIndividualAddressUKView

class SettlorIndividualAddressUKViewSpec extends QuestionViewBehaviours[SettlorIndividualAddressUK] {

  val messageKeyPrefix = "settlorIndividualAddressUK"

  override val form = new SettlorIndividualAddressUKFormProvider()()

  "SettlorIndividualAddressUKView" must {

    val view = viewFor[SettlorIndividualAddressUKView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.SettlorIndividualAddressUKController.onSubmit(NormalMode, fakeDraftId).url,
      Seq(("field1", None), ("field2", None))
    )
  }
}
