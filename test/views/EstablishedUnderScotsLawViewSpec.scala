package views

import controllers.routes
import forms.EstablishedUnderScotsLawFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.EstablishedUnderScotsLawView

class EstablishedUnderScotsLawViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "establishedUnderScotsLaw"

  val form = new EstablishedUnderScotsLawFormProvider()()

  "EstablishedUnderScotsLaw view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[EstablishedUnderScotsLawView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.EstablishedUnderScotsLawController.onSubmit(NormalMode).url)
  }
}
