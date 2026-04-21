/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms.mappings

import models.Enumerable
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.OptionValues
import play.api.data.{Form, FormError}

object MappingsSpec {

  sealed trait Foo
  case object Bar extends Foo
  case object Baz extends Foo

  object Foo {

    val values: Set[Foo] = Set(Bar, Baz)

    implicit val fooEnumerable: Enumerable[Foo] =
      Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  }

}

class MappingsSpec extends AnyWordSpec with Matchers with OptionValues with Mappings {

  import MappingsSpec._

  "text" must {

    val testForm: Form[String] =
      Form(
        "value" -> text()
      )

    "bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "filter out smart apostrophes on binding" in {
      val boundValue = testForm bind Map("value" -> "We’re ‘aving fish ‘n’ chips for tea")
      boundValue.get mustEqual "We're 'aving fish 'n' chips for tea"
    }

    "bind and trim spaces" in {
      val result = testForm.bind(Map("value" -> "   foobar     "))
      result.get mustEqual "foobar"
    }

    "not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "return a custom error message" in {
      val form   = Form("value" -> text("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "postcode" must {

    val testForm: Form[String] =
      Form(
        "value" -> postcode()
      )

    val validPostcodes = Seq(
      "AA9A 9AA",
      "A9A 9AA",
      "A9 9AA",
      "A99 9AA",
      "AA9 9AA",
      "AA99 9AA"
    )

    validPostcodes.foreach { p =>
      s"bind a valid postcode $p" in {
        val result = testForm.bind(Map("value" -> p))
        result.get mustEqual p
      }

    }

    "not bind an invalid postcode due to format" in {
      val result = testForm.bind(Map("value" -> "AA1 1A"))
      result.errors must contain(FormError("value", "error.postcodeInvalid"))
    }

    "not bind an invalid postcode due to too many spaces" in {
      val result = testForm.bind(Map("value" -> "AA1  1AA"))
      result.errors must contain(FormError("value", "error.postcodeInvalid"))
    }
  }

  "currency" must {

    val testForm: Form[String] =
      Form(
        "value" -> currency()
      )

    val validCurency = Seq(
      "1",
      "999",
      "999999",
      "999999999",
      "999999999999"
    )

    validCurency.foreach { p =>
      s"bind a valid currency $p" in {
        val result = testForm.bind(Map("value" -> p))
        result.get mustEqual p
      }
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "assetMoneyValue.error.required"))
    }

    "return a custom error message" in {
      val form   = Form("value" -> postcode("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "unbind a valid value" in {
      val result = testForm.fill("999999")
      result.apply("value").value.value mustEqual "999999"
    }
  }

  "boolean" must {

    val testForm: Form[Boolean] =
      Form(
        "value" -> boolean()
      )

    "bind true" in {
      val result = testForm.bind(Map("value" -> "true"))
      result.get mustEqual true
    }

    "bind false" in {
      val result = testForm.bind(Map("value" -> "false"))
      result.get mustEqual false
    }

    "not bind a non-boolean" in {
      val result = testForm.bind(Map("value" -> "not a boolean"))
      result.errors must contain(FormError("value", "error.boolean"))
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind" in {
      val result = testForm.fill(true)
      result.apply("value").value.value mustEqual "true"
    }
  }

  "int" must {

    val testForm: Form[Int] =
      Form(
        "value" -> int()
      )

    "bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "bind a valid number with commas" in {
      val result = testForm.bind(Map("value" -> "123,456"))
      result.get mustEqual 123456
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "enumerable" must {

    val testForm = Form(
      "value" -> enumerable[Foo]()
    )

    "bind a valid option" in {
      val result = testForm.bind(Map("value" -> "Bar"))
      result.get mustEqual Bar
    }

    "not bind an invalid option" in {
      val result = testForm.bind(Map("value" -> "Not Bar"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }
  }

  "utr" must {

    val allowedUtrLength = 10

    val testForm: Form[String] =
      Form(
        "value" -> utr("error.required", "error.invalid", "error.length")
      )

    "bind a valid utr" in {
      val result = testForm.bind(Map("value" -> "1234567890"))
      result.get mustEqual "1234567890"
    }

    "bind an utr with spaces" in {
      val result = testForm.bind(Map("value" -> " 123  4567  890 "))
      result.get mustEqual "1234567890"
    }

    "not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an invalid option" in {
      val result = testForm.bind(Map("value" -> "12345ABCDE"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "not bind a too long UTR" in {
      val result = testForm.bind(Map("value" -> "1234567890ABCDE"))
      result.errors must contain(FormError("value", "error.length", List(allowedUtrLength)))
    }

    "not bind a too short UTR" in {
      val result = testForm.bind(Map("value" -> "12345"))
      result.errors must contain(FormError("value", "error.length", List(allowedUtrLength)))
    }

    "unbind a valid value" in {
      val result = testForm.fill("1234567890")
      result.apply("value").value.value mustEqual "1234567890"
    }
  }

}
