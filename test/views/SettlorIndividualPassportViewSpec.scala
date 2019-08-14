package views

import controllers.routes
import forms.SettlorIndividualPassportFormProvider
import models.{NormalMode, SettlorIndividualPassport}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorIndividualPassportView

class SettlorIndividualPassportViewSpec extends QuestionViewBehaviours[SettlorIndividualPassport] {

  val messageKeyPrefix = "settlorIndividualPassport"

  override val form = new SettlorIndividualPassportFormProvider()()

  "SettlorIndividualPassportView" must {

    val view = viewFor[SettlorIndividualPassportView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.SettlorIndividualPassportController.onSubmit(NormalMode, fakeDraftId).url,
      Seq(("field1", None), ("field2", None))
    )
  }
}
