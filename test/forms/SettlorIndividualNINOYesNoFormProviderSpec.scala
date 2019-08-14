package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class SettlorIndividualNINOYesNoFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "settlorIndividualNINOYesNo.error.required"
  val invalidKey = "error.boolean"

  val form = new SettlorIndividualNINOYesNoFormProvider()()

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
