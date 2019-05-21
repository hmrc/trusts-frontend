package views

import controllers.routes
import forms.AgentDeclarationFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.AgentDeclarationView

class AgentDeclarationViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "agentDeclaration"

  val form = new AgentDeclarationFormProvider()()

  "AgentDeclarationView view" must {

    val view = viewFor[AgentDeclarationView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.AgentDeclarationController.onSubmit(NormalMode).url)
  }
}
