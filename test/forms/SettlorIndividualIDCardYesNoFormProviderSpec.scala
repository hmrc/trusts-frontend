package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class SettlorIndividualIDCardYesNoFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "settlorIndividualIDCardYesNo.error.required"
  val invalidKey = "error.boolean"

  val form = new SettlorIndividualIDCardYesNoFormProvider()()

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
