package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class TrusteesUkAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new TrusteesUkAddressFormProvider()()

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = "trusteesUkAddress.error.line1.required"
    val lengthKey = "trusteesUkAddress.error.line1.length"
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
  }

  ".line2" must {

    val fieldName = "field2"
    val requiredKey = "trusteesUkAddress.error.line2.required"
    val lengthKey = "trusteesUkAddress.error.line2.length"
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
  }
}
