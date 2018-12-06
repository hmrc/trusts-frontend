package views

import controllers.routes
import forms.AdministrationOutsideUKFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.AdministrationOutsideUKView

class AdministrationOutsideUKViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "administrationOutsideUK"

  val form = new AdministrationOutsideUKFormProvider()()

  "AdministrationOutsideUK view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[AdministrationOutsideUKView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.AdministrationOutsideUKController.onSubmit(NormalMode).url)
  }
}
