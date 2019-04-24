package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.AddABeneficiary

class AddABeneficiaryFormProvider @Inject() extends Mappings {

  def apply(): Form[AddABeneficiary] =
    Form(
      "value" -> enumerable[AddABeneficiary]("addABeneficiary.error.required")
    )
}
