package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class AddABeneficiarySpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "AddABeneficiary" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(AddABeneficiary.values.toSeq)

      forAll(gen) {
        addABeneficiary =>

          JsString(addABeneficiary.toString).validate[AddABeneficiary].asOpt.value mustEqual addABeneficiary
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!AddABeneficiary.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[AddABeneficiary] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(AddABeneficiary.values.toSeq)

      forAll(gen) {
        addABeneficiary =>

          Json.toJson(addABeneficiary) mustEqual JsString(addABeneficiary.toString)
      }
    }
  }
}
