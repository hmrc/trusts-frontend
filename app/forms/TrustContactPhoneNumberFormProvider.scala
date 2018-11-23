package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class TrustContactPhoneNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("trustContactPhoneNumber.error.required")
        .verifying(maxLength(100, "trustContactPhoneNumber.error.length"))
    )
}
