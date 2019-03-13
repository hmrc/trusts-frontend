package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class AgencysTelehponeNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("agencysTelehponeNumber.error.required")
        .verifying(maxLength(100, "agencysTelehponeNumber.error.length"))
    )
}
