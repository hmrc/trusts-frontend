package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class Non-residentTypeSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "Non-residentType" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(Non-residentType.values.toSeq)

      forAll(gen) {
        non-residentType =>

          JsString(non-residentType.toString).validate[Non-residentType].asOpt.value mustEqual non-residentType
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!Non-residentType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[Non-residentType] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(Non-residentType.values.toSeq)

      forAll(gen) {
        non-residentType =>

          Json.toJson(non-residentType) mustEqual JsString(non-residentType.toString)
      }
    }
  }
}
