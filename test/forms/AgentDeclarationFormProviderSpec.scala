package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class AgentDeclarationFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "agentDeclaration.error.required"
  val lengthKey = "agentDeclaration.error.length"
  val maxLength = 100

  val form = new AgentDeclarationFormProvider()()

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
