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

  private val registrationPrefix = "registration"
  private val answerSectionPrefix = "answer section"

  def validateRegistrationSettlorComponent(settlorsData: Option[JsObject]): List[String] = {
    settlorsData match {
      case Some(settlors) =>
        val deceased = (settlors \ "trust/entities/deceased").asOpt[JsObject]
        val settlorsSection = (settlors \ "trust/entities/settlors").asOpt[JsObject]
        val livingSettlors = settlorsSection.flatMap(s => (s \ "settlor").asOpt[JsArray])
        val companySettlors = settlorsSection.flatMap(s => (s \ "settlorCompany").asOpt[JsArray])

        deceased match {
          case Some(deceasedData) =>
            val hasLivingSettlors = livingSettlors.exists(_.value.nonEmpty) || companySettlors.exists(_.value.nonEmpty)
            if (hasLivingSettlors) {
              List(s"$registrationPrefix: deceased settlor cannot coexist with other settlors")
            } else {
              validateDeceasedSettlor(deceasedData, registrationPrefix)
            }

          case None =>
            val hasIndividualSettlors = livingSettlors.exists(_.value.nonEmpty)
            val hasCompanySettlors = companySettlors.exists(_.value.nonEmpty)

            if (!hasIndividualSettlors && !hasCompanySettlors) {
              List(s"$registrationPrefix: no settlor information provided. Trust should have either a deceased settlor, an individual settlor or a company settlor")
            } else {
              val individualValidation = livingSettlors.map(validateLivingSettlors(_, registrationPrefix, "settlor")).getOrElse(List.empty)
              val companyValidation = companySettlors.map(validateCompanySettlors(_, registrationPrefix)).getOrElse(List.empty)
              individualValidation ::: companyValidation
            }
        }

      case None =>
        List(s"$registrationPrefix: no settlor information provided")
    }
  }

  def validateAnswerSectionSettlorComponent(answerSectionSettlors: Option[JsObject]): List[String] = {
    answerSectionSettlors match {
      case Some(settlorData) =>
        val settlorsSection = (settlorData \ "data" \ "settlors").asOpt[JsObject]

        settlorsSection match {
          case Some(settlors) =>
            val setUpByLiving = (settlors \ "setUpByLivingSettlorYesNo").asOpt[Boolean].getOrElse(true)

            if (!setUpByLiving) {
              val deceased = (settlors \ "deceased").asOpt[JsObject]
              deceased match {
                case Some(deceasedData) => validateDeceasedSettlor(deceasedData, answerSectionPrefix)
                case None => List(s"$answerSectionPrefix: deceased settlor data missing")
              }
            } else {
              val livingSettlors = (settlors \ "living").asOpt[JsArray]
              val companySettlors = (settlors \ "settlorCompany").asOpt[JsArray]

              val hasLivingSettlors = livingSettlors.exists(_.value.nonEmpty)
              val hasCompanySettlors = companySettlors.exists(_.value.nonEmpty)

              if (!hasLivingSettlors && !hasCompanySettlors) {
                List(s"$answerSectionPrefix: no living settlor information provided")
              } else {
                val livingValidation = livingSettlors.map(validateLivingSettlors(_, answerSectionPrefix, "living")).getOrElse(List.empty)
                val companyValidation = companySettlors.map(validateCompanySettlors(_, answerSectionPrefix)).getOrElse(List.empty)
                livingValidation ::: companyValidation
              }
            }

          case None =>
            List(s"$answerSectionPrefix: no settlors section found")
        }

      case None =>
        List(s"$answerSectionPrefix: no data provided")
    }
  }

  private def validateDeceasedSettlor(deceased: JsObject, prefix: String): List[String] = {
    (deceased \ "name").asOpt[JsObject] match {
      case Some(name) =>
        List(
          if ((name \ "firstName").asOpt[String].isEmpty) Some(s"$prefix: deceased.name.firstName missing") else None,
          if ((name \ "lastName").asOpt[String].isEmpty) Some(s"$prefix: deceased.name.lastName missing") else None
        ).flatten
      case None =>
        List(s"$prefix: deceased.name missing")
    }
  }

  private def validateLivingSettlors(settlors: JsArray, prefix: String, settlorType: String): List[String] = {
    settlors.value.zipWithIndex.flatMap { case (settlorValue, index) =>
      settlorValue.asOpt[JsObject] match {
        case Some(settlor) =>
          (settlor \ "name").asOpt[JsObject] match {
            case Some(name) =>
              List(
                if ((name \ "firstName").asOpt[String].isEmpty) Some(s"$prefix: $settlorType[$index].name.firstName missing") else None,
                if ((name \ "lastName").asOpt[String].isEmpty) Some(s"$prefix: $settlorType[$index].name.lastName missing") else None
              ).flatten
            case None =>
              List(s"$prefix: $settlorType[$index].name missing")
          }
        case None =>
          List(s"$prefix: $settlorType[$index] data missing")
      }
    }.toList
  }

  private def validateCompanySettlors(companies: JsArray, prefix: String): List[String] = {
    companies.value.zipWithIndex.flatMap { case (companyValue, index) =>
      companyValue.asOpt[JsObject] match {
        case Some(company) =>
          List(
            if ((company \ "name").asOpt[String].isEmpty) Some(s"$prefix: settlorCompany[$index].name missing") else None,
          ).flatten
        case None =>
          List(s"$prefix: settlorCompany[$index] data missing")
      }
    }.toList
  }
}