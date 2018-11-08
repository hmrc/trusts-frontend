package views

import play.api.data.Form
import controllers.routes
import forms.TrustNameFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.trustName

class TrustNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trustName"

  val form = new TrustNameFormProvider()()

  def createView = () => trustName(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => trustName(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "TrustName view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.TrustNameController.onSubmit(NormalMode).url)
  }
}
