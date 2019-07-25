package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ShareCompanyNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "shareCompanyName.error.required"
  val lengthKey = "shareCompanyName.error.length"
  val maxLength = 100

  val form = new ShareCompanyNameFormProvider()()

  ".value" must {

    val fieldName = "value"

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
