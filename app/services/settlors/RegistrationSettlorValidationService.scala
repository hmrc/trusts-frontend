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

import models.{IncompleteSettlorData, InvalidSettlorData, MissingSettlorData, SettlorDataError}
import play.api.libs.json._

import javax.inject.Inject

/** Validates settlor data from registration payload
 * Settlors are saved under `trust/entities/deceased` and `trust/entities/settlors`
 */
class RegistrationSettlorValidationService @Inject() {

  import SettlorValidationHelpers._

  private val prefix = "registration"

  def validate(settlorsData: JsObject): List[SettlorDataError] = {
    val deceased           = (settlorsData \ "trust/entities/deceased").asOpt[JsObject]
    val settlorsSection    = (settlorsData \ "trust/entities/settlors").asOpt[JsObject]
    val individualSettlors = settlorsSection.flatMap(s => (s \ "settlor").asOpt[JsArray])
    val companySettlors    = settlorsSection.flatMap(s => (s \ "settlorCompany").asOpt[JsArray])

    deceased match {
      case Some(deceasedData) =>
        val hasOtherSettlors =
          individualSettlors.exists(_.value.nonEmpty) || companySettlors.exists(_.value.nonEmpty)

        if (hasOtherSettlors) {
          List(InvalidSettlorData(s"$prefix: deceased settlor cannot coexist with other settlors"))
        } else {
          validateDeceasedSettlor(deceasedData, prefix)
        }

      case None =>
        validateIndividualAndCompanySettlors(individualSettlors, companySettlors)
    }
  }

  private def validateIndividualAndCompanySettlors(
    individualSettlors: Option[JsArray],
    companySettlors: Option[JsArray]
  ): List[SettlorDataError] = {
    val hasIndividualSettlors = individualSettlors.exists(_.value.nonEmpty)
    val hasCompanySettlors    = companySettlors.exists(_.value.nonEmpty)

    if (!hasIndividualSettlors && !hasCompanySettlors) {
      List(
        MissingSettlorData(
          s"$prefix: no settlor information provided. " +
            s"Trust should have either a deceased settlor, an individual settlor or a company settlor"
        )
      )
    } else {
      individualSettlors.map(validateIndividualArray).getOrElse(Nil) :::
        companySettlors.map(validateCompanyArray).getOrElse(Nil)
    }
  }

  private def validateIndividualArray(settlors: JsArray): List[SettlorDataError] =
    validateEntries(settlors.value.toList, prefix, IndividualSettlor) { (settlor, index) =>
      (settlor \ "name").asOpt[JsObject] match {
        case Some(name) => validateFirstAndLastName(name, prefix, IndividualSettlor, Some(index))
        case None       => List(IncompleteSettlorData(s"$prefix: $IndividualSettlor[$index].name missing"))
      }
    }

  private def validateCompanyArray(companies: JsArray): List[SettlorDataError] =
    validateEntries(companies.value.toList, prefix, CompanySettlor) { (company, index) =>
      if (keyMissingOrValueBlank(company, "name")) {
        List(IncompleteSettlorData(s"$prefix: $CompanySettlor[$index].name missing"))
      } else {
        Nil
      }
    }

}
