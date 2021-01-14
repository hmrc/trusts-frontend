/*
 * Copyright 2021 HM Revenue & Customs
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

package utils

import java.time.{LocalDate => JavaDate}

import org.joda.time.{LocalDate => JodaDate}
import uk.gov.hmrc.time.TaxYear

object TaxLiabilityHelper {

  def showTaxLiability(trustSetUpDate: Option[JavaDate]): Boolean = {
    trustSetUpDate match {
      case None =>
        false
      case Some(trustStartDate) =>
        def currentTaxYearStartDate: JavaDate = {
          implicit class DateConversion(date: JodaDate) {
            def toJavaDate: JavaDate = JavaDate.of(date.getYear, date.getMonthOfYear, date.getDayOfMonth)
          }
          TaxYear.current.starts.toJavaDate
        }
        trustStartDate isBefore currentTaxYearStartDate
    }
  }
}


