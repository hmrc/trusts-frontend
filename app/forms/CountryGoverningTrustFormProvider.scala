package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class CountryGoverningTrustFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("countryGoverningTrust.error.required")
        .verifying(maxLength(100, "countryGoverningTrust.error.length"))
    )
}
