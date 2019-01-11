package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class TrustHaveAUTRFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "trustHaveAUTR.error.required"
  val invalidKey = "error.boolean"

  val form = new TrustHaveAUTRFormProvider()()

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
