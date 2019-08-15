package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.SettlorIndividualAddressUK

class SettlorIndividualAddressUKFormProvider @Inject() extends Mappings {

  def apply(): Form[SettlorIndividualAddressUK] = Form(
    mapping(
      "field1" -> text("settlorIndividualAddressUK.error.field1.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualAddressUK.error.field1.length"),
  isNotEmpty("field1","settlorIndividualAddressUK.error.field1.required")
    )
  ),
  "field2" -> text("settlorIndividualAddressUK.error.field2.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualAddressUK.error.field2.length"),
  isNotEmpty("field2","settlorIndividualAddressUK.error.field2.required")
    )
  )
  )(SettlorIndividualAddressUK.apply)(SettlorIndividualAddressUK.unapply)
  )
}