package forms

import forms.behaviours.OptionFieldBehaviours
import models.WhatKindOfAsset
import play.api.data.FormError

class WhatKindOfAssetFormProviderSpec extends OptionFieldBehaviours {

  val form = new WhatKindOfAssetFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "whatKindOfAsset.error.required"

    behave like optionsField[WhatKindOfAsset](
      form,
      fieldName,
      validValues  = WhatKindOfAsset.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
