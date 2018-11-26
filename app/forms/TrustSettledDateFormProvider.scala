package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class TrustSettledDateFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("trustSettledDate.error.required")
        .verifying(maxLength(100, "trustSettledDate.error.length"))
    )
}
