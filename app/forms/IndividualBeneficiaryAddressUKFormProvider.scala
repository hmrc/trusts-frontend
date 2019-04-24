package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.IndividualBeneficiaryAddressUK

class IndividualBeneficiaryAddressUKFormProvider @Inject() extends Mappings {

  def apply(): Form[IndividualBeneficiaryAddressUK] = Form(
    mapping(
      "field1" -> text("individualBeneficiaryAddressUK.error.field1.required")
  .verifying(
    firstError(
      maxLength(100, "individualBeneficiaryAddressUK.error.field1.length"),
  isNotEmpty("field1","individualBeneficiaryAddressUK.error.field1.required")
    )
  ),
  "field2" -> text("individualBeneficiaryAddressUK.error.field2.required")
  .verifying(
    firstError(
      maxLength(100, "individualBeneficiaryAddressUK.error.field2.length"),
  isNotEmpty("field2","individualBeneficiaryAddressUK.error.field2.required")
    )
  )
  )(IndividualBeneficiaryAddressUK.apply)(IndividualBeneficiaryAddressUK.unapply)
  )
}