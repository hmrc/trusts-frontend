package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class InheritanceTaxActFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "inheritanceTaxAct.error.required"
  val invalidKey = "error.boolean"

  val form = new InheritanceTaxActFormProvider()()

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
