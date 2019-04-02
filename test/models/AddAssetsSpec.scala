package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class AddAssetsSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "AddAssets" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(AddAssets.values.toSeq)

      forAll(gen) {
        addAssets =>

          JsString(addAssets.toString).validate[AddAssets].asOpt.value mustEqual addAssets
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!AddAssets.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[AddAssets] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(AddAssets.values.toSeq)

      forAll(gen) {
        addAssets =>

          Json.toJson(addAssets) mustEqual JsString(addAssets.toString)
      }
    }
  }
}
