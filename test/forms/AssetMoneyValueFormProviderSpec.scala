package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class AssetMoneyValueFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "assetMoneyValue.error.required"
  val lengthKey = "assetMoneyValue.error.length"
  val maxLength = 12

  val form = new AssetMoneyValueFormProvider()()

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
