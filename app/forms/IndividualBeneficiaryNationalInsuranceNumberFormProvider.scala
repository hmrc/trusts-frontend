package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IndividualBeneficiaryNationalInsuranceNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("individualBeneficiaryNationalInsuranceNumber.error.required")
        .verifying(maxLength(100, "individualBeneficiaryNationalInsuranceNumber.error.length"))
    )
}
