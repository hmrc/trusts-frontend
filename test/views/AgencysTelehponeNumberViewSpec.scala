package views

import controllers.routes
import forms.AgencysTelehponeNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.AgencysTelehponeNumberView

class AgencysTelehponeNumberViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "agencysTelehponeNumber"

  val form = new AgencysTelehponeNumberFormProvider()()

  "AgencysTelehponeNumberView view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[AgencysTelehponeNumberView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.AgencysTelehponeNumberController.onSubmit(NormalMode).url)
  }
}
