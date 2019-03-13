package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class AgencysTelehponeNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "agencysTelehponeNumber.error.required"
  val lengthKey = "agencysTelehponeNumber.error.length"
  val maxLength = 100

  val form = new AgencysTelehponeNumberFormProvider()()

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
