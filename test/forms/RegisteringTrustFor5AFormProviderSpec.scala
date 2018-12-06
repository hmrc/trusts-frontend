package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class RegisteringTrustFor5AFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "registeringTrustFor5A.error.required"
  val invalidKey = "error.boolean"

  val form = new RegisteringTrustFor5AFormProvider()()

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
