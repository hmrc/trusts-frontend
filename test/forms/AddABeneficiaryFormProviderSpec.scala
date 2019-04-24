package forms

import forms.behaviours.OptionFieldBehaviours
import models.AddABeneficiary
import play.api.data.FormError

class AddABeneficiaryFormProviderSpec extends OptionFieldBehaviours {

  val form = new AddABeneficiaryFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "addABeneficiary.error.required"

    behave like optionsField[AddABeneficiary](
      form,
      fieldName,
      validValues  = AddABeneficiary.values.toSet,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
