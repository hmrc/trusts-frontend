package views

import java.time.LocalDate

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat

class $className$ViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "$className;format="decap"$"

  val form = new $className$FormProvider()()

  "$className$View view" must {

    val view = viewFor[$className$View](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), None, messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))
  }
}
