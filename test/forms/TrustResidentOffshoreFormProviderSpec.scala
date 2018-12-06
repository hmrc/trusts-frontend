package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class TrustResidentOffshoreFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "trustResidentOffshore.error.required"
  val invalidKey = "error.boolean"

  val form = new TrustResidentOffshoreFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
