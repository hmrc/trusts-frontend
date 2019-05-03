package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IndividualBeneficiaryIsAddressUKFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("individualBeneficiaryIsAddressUK.error.required")
    )
}
