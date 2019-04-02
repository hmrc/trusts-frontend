package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.AddAssets

class AddAssetsFormProvider @Inject() extends Mappings {

  def apply(): Form[AddAssets] =
    Form(
      "value" -> enumerable[AddAssets]("addAssets.error.required")
    )
}
