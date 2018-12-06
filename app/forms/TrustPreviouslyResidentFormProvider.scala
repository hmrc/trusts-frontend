package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class TrustPreviouslyResidentFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("trustPreviouslyResident.error.required")
        .verifying(maxLength(100, "trustPreviouslyResident.error.length"))
    )
}
