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

package services.settlors

import models.settlor.{IncompleteSettlorData, InvalidSettlorData, MissingSettlorData, SettlorDataError}
import play.api.libs.json._

import javax.inject.Inject

class AnswerSectionSettlorValidationService @Inject() {

  import SettlorValidationHelpers._

  private val prefix = "answer section"

  def validate(answerSectionSettlors: JsValue): List[SettlorDataError] =
    (answerSectionSettlors \ "data" \ "settlors").asOpt[JsObject] match {
      case Some(settlors) =>
        (settlors \ "setUpByLivingSettlorYesNo").asOpt[Boolean] match {
          case Some(false) => validateDeceasedFlow(settlors)
          case Some(true)  => validateLivingFlow(settlors)
          case None        => List(IncompleteSettlorData(s"$prefix: setUpByLivingSettlorYesNo missing"))
        }
      case None           =>
        List(MissingSettlorData(s"$prefix: no settlors section found"))
    }

  private def validateDeceasedFlow(settlors: JsObject): List[SettlorDataError] =
    (settlors \ "deceased").asOpt[JsObject] match {
      case Some(deceased) => validateDeceasedSettlor(deceased, prefix)
      case None           => List(IncompleteSettlorData(s"$prefix: deceased settlor data missing"))
    }

  private def validateLivingFlow(settlors: JsObject): List[SettlorDataError] =
    (settlors \ "living").asOpt[JsArray] match {
      case Some(livingSettlors) if livingSettlors.value.nonEmpty =>
        validateCombinedSettlorArray(livingSettlors)
      case _                                                     =>
        List(IncompleteSettlorData(s"$prefix: no living settlor information provided"))
    }

  private def validateCombinedSettlorArray(settlors: JsArray): List[SettlorDataError] = {
    val settlorsAsList = settlors.value.toList

    val individuals = settlorsAsList.collect {
      case settlor if individualOrBusinessSectionContainsKey(settlor, "individual") => settlor
    }

    val individualValidation =
      validateEntries(individuals, prefix, IndividualSettlor) { (settlor, index) =>
        (settlor \ "name").asOpt[JsObject] match {
          case Some(name) => validateFirstAndLastName(name, prefix, IndividualSettlor, Some(index))
          case None       => List(IncompleteSettlorData(s"$prefix: $IndividualSettlor[$index].name missing"))
        }
      }

    val companies = settlorsAsList.collect {
      case settlor if individualOrBusinessSectionContainsKey(settlor, "business") => settlor
    }

    val companyValidation = validateEntries(companies, prefix, CompanySettlor) { (settlor, index) =>
      if (keyMissingOrValueBlank(settlor, "businessName"))
        List(IncompleteSettlorData(s"$prefix: $CompanySettlor[$index].businessName missing"))
      else Nil
    }

    val invalidEntries = validateEntries(settlorsAsList, prefix, "settlor") { (settlor, index) =>
      (settlor \ "individualOrBusiness").asOpt[String] match {
        case Some("individual") | Some("business") => Nil
        case _                                     =>
          List(IncompleteSettlorData(s"$prefix: settlor[$index] individualOrBusiness missing or invalid"))
      }
    }

    individualValidation ++ companyValidation ++ invalidEntries
  }

  private def individualOrBusinessSectionContainsKey(jsValue: JsValue, expected: String): Boolean =
    jsValue
      .asOpt[JsObject]
      .flatMap(s => (s \ "individualOrBusiness").asOpt[String])
      .contains(expected)

}
