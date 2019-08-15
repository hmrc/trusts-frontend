package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.SettlorIndividualPassport

class SettlorIndividualPassportFormProvider @Inject() extends Mappings {

  def apply(): Form[SettlorIndividualPassport] = Form(
    mapping(
      "field1" -> text("settlorIndividualPassport.error.field1.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualPassport.error.field1.length"),
  isNotEmpty("field1","settlorIndividualPassport.error.field1.required")
    )
  ),
  "field2" -> text("settlorIndividualPassport.error.field2.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualPassport.error.field2.length"),
  isNotEmpty("field2","settlorIndividualPassport.error.field2.required")
    )
  )
  )(SettlorIndividualPassport.apply)(SettlorIndividualPassport.unapply)
  )
}