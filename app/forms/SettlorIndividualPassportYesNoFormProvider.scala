package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class SettlorIndividualPassportYesNoFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("settlorIndividualPassportYesNo.error.required")
    )
}
