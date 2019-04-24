package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IndividualBeneficiaryIncomeFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("individualBeneficiaryIncome.error.required")
        .verifying(maxLength(100, "individualBeneficiaryIncome.error.length"))
    )
}
