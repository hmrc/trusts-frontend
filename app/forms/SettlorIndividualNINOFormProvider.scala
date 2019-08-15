package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class SettlorIndividualNINOFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("settlorIndividualNINO.error.required")
        .verifying(maxLength(100, "settlorIndividualNINO.error.length"))
    )
}
