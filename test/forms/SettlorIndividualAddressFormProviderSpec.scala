package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class SettlorIndividualAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new SettlorIndividualAddressFormProvider()()

  ".field1" must {

    val fieldName = "field1"
    val requiredKey = "settlorIndividualAddress.error.field1.required"
    val lengthKey = "settlorIndividualAddress.error.field1.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

  ".field2" must {

    val fieldName = "field2"
    val requiredKey = "settlorIndividualAddress.error.field2.required"
    val lengthKey = "settlorIndividualAddress.error.field2.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }
}
