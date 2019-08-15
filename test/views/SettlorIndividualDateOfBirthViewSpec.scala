package views

import java.time.LocalDate

import forms.SettlorIndividualDateOfBirthFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.SettlorIndividualDateOfBirthView

class SettlorIndividualDateOfBirthViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "settlorIndividualDateOfBirth"

  val form = new SettlorIndividualDateOfBirthFormProvider()()

  "SettlorIndividualDateOfBirthView view" must {

    val view = viewFor[SettlorIndividualDateOfBirthView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))
  }
}
