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

import models.{IncompleteSettlorData, SettlorDataError}
import play.api.libs.json._

private[services] object SettlorValidationHelpers {

  val IndividualSettlor = "individualSettlor"
  val CompanySettlor    = "companySettlor"

  /** 
   * Validate the deceased settlor name, 
   * returning errors if the data is missing, or either the first or last name are missing or blank strings 
   *
   * @param deceased the deceased settlor JSON object
   * @param prefix   source-context prefix prepended to each error message (e.g. `"registration"` or `"answer section"`)
   */
  def validateDeceasedSettlor(deceased: JsObject, prefix: String): List[SettlorDataError] =
    (deceased \ "name").asOpt[JsObject] match {
      case Some(name) => validateFirstAndLastName(name, prefix, "deceased")
      case None       => List(IncompleteSettlorData(s"$prefix: deceased.name missing"))
    }

  /** 
   * Validate a person's first name and last name
   *
   * @param prefix   source-context prefix prepended to each error message (e.g. `"registration"` or `"answer section"`)
   * @param settlorType entity label used in the error path (e.g. `"deceased"`, `"individualSettlor"`)
   * @param index      when defined, format error message as `settlorType[index]` instead of `settlorType`
   */
  def validateFirstAndLastName(
    name: JsObject,
    prefix: String,
    settlorType: String,
    index: Option[Int] = None
  ): List[SettlorDataError] =
    List("firstName", "lastName").collect {
      case fieldName if keyMissingOrValueBlank(name, fieldName) =>
        index match {
          case Some(idx) => IncompleteSettlorData(s"$prefix: $settlorType[$idx].name.$fieldName missing")
          case None      => IncompleteSettlorData(s"$prefix: $settlorType.name.$fieldName missing")
        }
    }

  /**
   * Iterate over a list of entries, if the entry is defined, run the `onEntryDefinedFn` against it.
   * Else return a List[IncompleteSettlorData] with relevant error detail
   * 
   * @param prefix        source-context prefix prepended to "data missing" errors
   * @param entityRef     entity label used in error paths (e.g. `"individualSettlor"`, `"companySettlor"`, `"settlor"`)
   * @param onEntryDefinedFn runs once per entry that resolves to a JsObject; receives
   *                      the object and its index in `entries`
   */
  def validateEntries(
    arrayEntries: List[JsValue],
    prefix: String,
    entityRef: String
  )(onEntryDefinedFn: (JsObject, Int) => List[SettlorDataError]): List[SettlorDataError] =

    arrayEntries.zipWithIndex.flatMap { case (value, index) =>
      value.asOpt[JsObject] match {
        case Some(obj) => onEntryDefinedFn(obj, index)
        case None      => List(IncompleteSettlorData(s"$prefix: $entityRef[$index] data missing"))
      }
    }


  def keyMissingOrValueBlank(jsObject: JsObject, key: String): Boolean =
    (jsObject \ key).asOpt[String].forall(_.isBlank)

}
