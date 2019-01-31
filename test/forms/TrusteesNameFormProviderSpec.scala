package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class TrusteesNameFormProviderSpec extends StringFieldBehaviours {


  val form = new TrusteesNameFormProvider()()

  ".firstName" must {

    val fieldName = "firstName"
    val requiredKey = "trusteesName.error.firstnamerequired"
    val lengthKey = "trusteesName.error.lengthfirstname"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".middleName" must {

    val fieldName = "middleName"
    val lengthKey = "trusteesName.error.lengthmiddlename"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = String)
  }

  ".lastName" must {

    val fieldName = "lastName"
    val requiredKey = "trusteesName.error.LastNamerequired"
    val lengthKey = "trusteesName.error.lengthlastname"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
