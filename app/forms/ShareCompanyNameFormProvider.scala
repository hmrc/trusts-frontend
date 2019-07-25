package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class ShareCompanyNameFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("shareCompanyName.error.required")
        .verifying(maxLength(100, "shareCompanyName.error.length"))
    )
}
