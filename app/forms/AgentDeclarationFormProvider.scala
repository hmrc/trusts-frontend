package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class AgentDeclarationFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("agentDeclaration.error.required")
        .verifying(maxLength(100, "agentDeclaration.error.length"))
    )
}
