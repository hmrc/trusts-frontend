package views

import controllers.routes
import forms.SettlorIndividualIDCardFormProvider
import models.{NormalMode, SettlorIndividualIDCard}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorIndividualIDCardView

class SettlorIndividualIDCardViewSpec extends QuestionViewBehaviours[SettlorIndividualIDCard] {

  val messageKeyPrefix = "settlorIndividualIDCard"

  override val form = new SettlorIndividualIDCardFormProvider()()

  "SettlorIndividualIDCardView" must {

    val view = viewFor[SettlorIndividualIDCardView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.SettlorIndividualIDCardController.onSubmit(NormalMode, fakeDraftId).url,
      Seq(("field1", None), ("field2", None))
    )
  }
}
