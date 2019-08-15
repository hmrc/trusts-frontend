package views

import controllers.routes
import forms.SettlorIndividualAddressInternationalFormProvider
import models.{NormalMode, SettlorIndividualAddressInternational}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorIndividualAddressInternationalView

class SettlorIndividualAddressInternationalViewSpec extends QuestionViewBehaviours[SettlorIndividualAddressInternational] {

  val messageKeyPrefix = "settlorIndividualAddressInternational"

  override val form = new SettlorIndividualAddressInternationalFormProvider()()

  "SettlorIndividualAddressInternationalView" must {

    val view = viewFor[SettlorIndividualAddressInternationalView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.SettlorIndividualAddressInternationalController.onSubmit(NormalMode, fakeDraftId).url,
      Seq(("field1", None), ("field2", None))
    )
  }
}
