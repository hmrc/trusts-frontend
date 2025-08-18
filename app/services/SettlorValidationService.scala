/*
 * Copyright 2025 HM Revenue & Customs
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

package services

import play.api.libs.json._
import javax.inject.Inject

class SettlorValidationService @Inject() {

  def validateSettlorComponents(settlorsData: Option[JsObject]): List[String] = {
    settlorsData match {
      case Some(settlors) =>
        val deceased = (settlors \ "deceased").asOpt[JsObject]
        val individuals = (settlors \ "settlor").asOpt[JsArray]
        val companies = (settlors \ "settlorCompany").asOpt[JsArray]

        deceased match {
          case Some(deceasedData) =>
            val hasOtherSettlors = individuals.exists(_.value.nonEmpty) || companies.exists(_.value.nonEmpty)
            if (hasOtherSettlors) {
              List("deceased settlor cannot coexist with other settlors")
            } else {
              validateDeceasedSettlor(deceasedData)
            }

          case None =>
            val hasIndividuals = individuals.exists(_.value.nonEmpty)
            val hasCompanies = companies.exists(_.value.nonEmpty)

            if (!hasIndividuals && !hasCompanies) {
              List("no settlor information provided. Trust should have either a deceased settlor, an individual settlor or a company settlor")
            } else {
              val individualValidation = individuals.map(validateIndividualSettlors).getOrElse(List.empty)
              val companyValidation = companies.map(validateCompanySettlors).getOrElse(List.empty)
              individualValidation ::: companyValidation
            }
        }

      case None =>
        List("no settlor information provided. Trust should have either a deceased settlor, an individual settlor or a company settlor")
    }
  }

  private def validateDeceasedSettlor(deceased: JsObject): List[String] = {
    validatePersonSettlor(deceased, "deceased", includeDeathDate = true)
  }

  private def validateIndividualSettlor(settlor: JsObject, index: Int): List[String] = {
    validatePersonSettlor(settlor, s"settlor[$index]", includeDeathDate = false)
  }

  private def validateIndividualSettlors(individuals: JsArray): List[String] = {
    individuals.value.zipWithIndex.flatMap {
      case (settlor, index) => validateIndividualSettlor(settlor.asOpt[JsObject].getOrElse(Json.obj()), index)
    }.toList
  }

  private def validateCompanySettlors(companies: JsArray): List[String] = {
    companies.value.zipWithIndex.flatMap {
      case (company, index) => validateCompanySettlor(company.asOpt[JsObject].getOrElse(Json.obj()), index)
    }.toList
  }

  private def validatePersonSettlor(person: JsObject, prefix: String, includeDeathDate: Boolean): List[String] = {
    val nameValidation = (person \ "name").asOpt[JsObject] match {
      case Some(name) =>
        List(
          if ((name \ "firstName").asOpt[String].isEmpty) Some(s"$prefix.name.firstName missing") else None,
          if ((name \ "lastName").asOpt[String].isEmpty) Some(s"$prefix.name.lastName missing") else None
        ).flatten
      case None =>
        List(s"$prefix.name missing")
    }

    val commonFields = List(
      if ((person \ "dateOfBirth").asOpt[String].isEmpty) Some(s"$prefix.dateOfBirth missing") else None,
      if ((person \ "identification").asOpt[JsObject].isEmpty) Some(s"$prefix.identification missing") else None,
      if ((person \ "countryOfResidence").asOpt[String].isEmpty) Some(s"$prefix.countryOfResidence missing") else None,
      if ((person \ "nationality").asOpt[String].isEmpty) Some(s"$prefix.nationality missing") else None
    ).flatten

    val deathDateValidation = if (includeDeathDate) {
      List(if ((person \ "dateOfDeath").asOpt[String].isEmpty) Some(s"$prefix.dateOfDeath missing") else None).flatten
    } else {
      List.empty
    }

    nameValidation ::: commonFields ::: deathDateValidation
  }

  private def validateCompanySettlor(company: JsObject, index: Int): List[String] = {
    List(
      if ((company \ "name").asOpt[String].isEmpty) Some(s"settlorCompany[$index].name missing") else None,
      if ((company \ "companyType").asOpt[String].isEmpty) Some(s"settlorCompany[$index].companyType missing") else None,
      if ((company \ "identification").asOpt[JsObject].isEmpty) Some(s"settlorCompany[$index].identification missing") else None,
      if ((company \ "countryOfResidence").asOpt[String].isEmpty) Some(s"settlorCompany[$index].countryOfResidence missing") else None
    ).flatten
  }
}
