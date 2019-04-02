package forms

import forms.behaviours.OptionFieldBehaviours
import models.AddAssets
import play.api.data.FormError

class AddAssetsFormProviderSpec extends OptionFieldBehaviours {

  val form = new AddAssetsFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "addAssets.error.required"

    behave like optionsField[AddAssets](
      form,
      fieldName,
      validValues  = AddAssets.values.toSet,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
