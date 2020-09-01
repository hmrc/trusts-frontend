package views

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat

class $className$ViewSpec extends QuestionViewBehaviours[$className$] {

  val messageKeyPrefix = "$className;format="decap"$"

  override val form = new $className$FormProvider()()

  "$className$View" must {

    val view = viewFor[$className$View](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)


    behave like normalPage(applyView(form), None, messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      Seq(("field1", None), ("field2", None))
    )
  }
}
