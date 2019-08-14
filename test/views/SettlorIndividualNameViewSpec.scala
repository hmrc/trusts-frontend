package views

import controllers.routes
import forms.SettlorIndividualNameFormProvider
import models.{FullName, NormalMode, SettlorIndividualName}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorIndividualNameView

class SettlorIndividualNameViewSpec extends QuestionViewBehaviours[FullName] {

  val messageKeyPrefix = "settlorIndividualName"

  override val form = new SettlorIndividualNameFormProvider()()

  "SettlorIndividualNameView" must {

    val view = viewFor[SettlorIndividualNameView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)


    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      routes.SettlorIndividualNameController.onSubmit(NormalMode, fakeDraftId).url,
      Seq(("firstName", None), ("middleName", None), ("lastName", None))
    )
  }
}
