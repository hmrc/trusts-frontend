package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class TelephoneNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("telephoneNumber.error.required")
        .verifying(maxLength(100, "telephoneNumber.error.length"))
    )
}
