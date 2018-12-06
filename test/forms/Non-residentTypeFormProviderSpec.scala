package forms

import forms.behaviours.OptionFieldBehaviours
import models.Non-residentType
import play.api.data.FormError

class Non-residentTypeFormProviderSpec extends OptionFieldBehaviours {

  val form = new Non-residentTypeFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "non-residentType.error.required"

    behave like optionsField[Non-residentType](
      form,
      fieldName,
      validValues  = Non-residentType.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
