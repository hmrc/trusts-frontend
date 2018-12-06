package views

import controllers.routes
import forms.InheritanceTaxActFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.InheritanceTaxActView

class InheritanceTaxActViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "inheritanceTaxAct"

  val form = new InheritanceTaxActFormProvider()()

  "InheritanceTaxAct view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[InheritanceTaxActView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.InheritanceTaxActController.onSubmit(NormalMode).url)
  }
}
