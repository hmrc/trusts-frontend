package views

import play.api.data.Form
import controllers.routes
import forms.TrustAddressUKYesNoFormProvider
import views.behaviours.YesNoViewBehaviours
import models.NormalMode
import views.html.trustAddressUKYesNo

class TrustAddressUKYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trustAddressUKYesNo"

  val form = new TrustAddressUKYesNoFormProvider()()

  def createView = () => trustAddressUKYesNo(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => trustAddressUKYesNo(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "TrustAddressUKYesNo view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.TrustAddressUKYesNoController.onSubmit(NormalMode).url)
  }
}
