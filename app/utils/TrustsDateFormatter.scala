/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.google.inject.Inject
import config.FrontendAppConfig

trait DateFormatter {

  def formatDate(dateTime: LocalDateTime): String

  def savedUntil(date: LocalDateTime) : String
}

class TrustsDateFormatter @Inject()(config: FrontendAppConfig) extends DateFormatter {

  private val format = "d MMMM yyyy"

  def formatDate(dateTime: LocalDateTime): String = {
    val dateFormatter = DateTimeFormatter.ofPattern(format)
    dateTime.format(dateFormatter)
  }

  def savedUntil(date: LocalDateTime) : String = {
    val ttlInSeconds = config.ttlInSeconds
    formatDate(date.plusSeconds(ttlInSeconds))
  }

}
