package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class SettlorHandoverReliefYesNoFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "settlorHandoverReliefYesNo.error.required"
  val invalidKey = "error.boolean"

  val form = new SettlorHandoverReliefYesNoFormProvider()()

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
