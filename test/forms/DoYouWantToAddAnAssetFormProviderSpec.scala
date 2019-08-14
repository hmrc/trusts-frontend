package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class DoYouWantToAddAnAssetFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "doYouWantToAddAnAsset.error.required"
  val invalidKey = "error.boolean"

  val form = new DoYouWantToAddAnAssetFormProvider()()

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
