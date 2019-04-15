package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.$className$

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[$className$] = Form(
    mapping(
      "field1" -> text("$className;format="decap"$.error.field1.required")
  .verifying(
    firstError(
      maxLength($field1MaxLength$, "$className;format="decap"$.error.field1.length"),
  isNotEmpty("field1","$className;format="decap"$.error.field1.required")
    )
  ),
  "field2" -> text("$className;format="decap"$.error.field2.required")
  .verifying(
    firstError(
      maxLength($field2MaxLength$, "$className;format="decap"$.error.field2.length"),
  isNotEmpty("field2","$className;format="decap"$.error.field2.required")
    )
  )
  )($className$.apply)($className$.unapply)
  )
}