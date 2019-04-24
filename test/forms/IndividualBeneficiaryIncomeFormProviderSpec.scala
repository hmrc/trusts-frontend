package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class IndividualBeneficiaryIncomeFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "individualBeneficiaryIncome.error.required"
  val lengthKey = "individualBeneficiaryIncome.error.length"
  val maxLength = 100

  val form = new IndividualBeneficiaryIncomeFormProvider()()

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
