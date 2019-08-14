package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class SettlorIndividualDateOfBirthFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "settlorIndividualDateOfBirth.error.invalid",
        allRequiredKey = "settlorIndividualDateOfBirth.error.required.all",
        twoRequiredKey = "settlorIndividualDateOfBirth.error.required.two",
        requiredKey    = "settlorIndividualDateOfBirth.error.required"
      )
    )
}
