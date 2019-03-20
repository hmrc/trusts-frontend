package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.WhatKindOfAsset

class WhatKindOfAssetFormProvider @Inject() extends Mappings {

  def apply(): Form[WhatKindOfAsset] =
    Form(
      "value" -> enumerable[WhatKindOfAsset]("whatKindOfAsset.error.required")
    )
}
