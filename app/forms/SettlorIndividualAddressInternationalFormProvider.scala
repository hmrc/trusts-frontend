package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.SettlorIndividualAddressInternational

class SettlorIndividualAddressInternationalFormProvider @Inject() extends Mappings {

  def apply(): Form[SettlorIndividualAddressInternational] = Form(
    mapping(
      "field1" -> text("settlorIndividualAddressInternational.error.field1.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualAddressInternational.error.field1.length"),
  isNotEmpty("field1","settlorIndividualAddressInternational.error.field1.required")
    )
  ),
  "field2" -> text("settlorIndividualAddressInternational.error.field2.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualAddressInternational.error.field2.length"),
  isNotEmpty("field2","settlorIndividualAddressInternational.error.field2.required")
    )
  )
  )(SettlorIndividualAddressInternational.apply)(SettlorIndividualAddressInternational.unapply)
  )
}