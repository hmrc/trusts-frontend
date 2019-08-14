package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.SettlorIndividualIDCard

class SettlorIndividualIDCardFormProvider @Inject() extends Mappings {

  def apply(): Form[SettlorIndividualIDCard] = Form(
    mapping(
      "field1" -> text("settlorIndividualIDCard.error.field1.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualIDCard.error.field1.length"),
  isNotEmpty("field1","settlorIndividualIDCard.error.field1.required")
    )
  ),
  "field2" -> text("settlorIndividualIDCard.error.field2.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualIDCard.error.field2.length"),
  isNotEmpty("field2","settlorIndividualIDCard.error.field2.required")
    )
  )
  )(SettlorIndividualIDCard.apply)(SettlorIndividualIDCard.unapply)
  )
}