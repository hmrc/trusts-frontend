package views

import play.api.data.Form
import controllers.routes
import forms.TrustSettledDateFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.trustSettledDate

class TrustSettledDateViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "trustSettledDate"

  val form = new TrustSettledDateFormProvider()()

  def createView = () => trustSettledDate(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => trustSettledDate(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "TrustSettledDate view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.TrustSettledDateController.onSubmit(NormalMode).url)
  }
}
