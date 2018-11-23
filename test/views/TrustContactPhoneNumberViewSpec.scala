package views

import play.api.data.Form
import controllers.routes
import forms.TrustContactPhoneNumberFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.trustContactPhoneNumber

class TrustContactPhoneNumberViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trustContactPhoneNumber"

  val form = new TrustContactPhoneNumberFormProvider()()

  def createView = () => trustContactPhoneNumber(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => trustContactPhoneNumber(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "TrustContactPhoneNumber view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.TrustContactPhoneNumberController.onSubmit(NormalMode).url)
  }
}
