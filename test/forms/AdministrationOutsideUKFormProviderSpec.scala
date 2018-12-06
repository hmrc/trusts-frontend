package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AdministrationOutsideUKFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "administrationOutsideUK.error.required"
  val invalidKey = "error.boolean"

  val form = new AdministrationOutsideUKFormProvider()()

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
