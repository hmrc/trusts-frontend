package views

import controllers.routes
import forms.AgentOtherThanBarristerFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.AgentOtherThanBarristerView

class AgentOtherThanBarristerViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "agentOtherThanBarrister"

  val form = new AgentOtherThanBarristerFormProvider()()

  "AgentOtherThanBarrister view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[AgentOtherThanBarristerView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.AgentOtherThanBarristerController.onSubmit(NormalMode).url)
  }
}
