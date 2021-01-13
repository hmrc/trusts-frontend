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

import models.core.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import pages.register.agents.AgentNamePage
import pages.register.asset.business.BusinessNamePage
import pages.register.asset.shares.ShareCompanyNamePage
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import utils.countryOptions.CountryOptions

object CheckAnswersFormatters {

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }
  }

  def country(code: String, countryOptions: CountryOptions): String =
    countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse("")

  def currency(value: String): Html = escape(currencyFormat(value))

  def currencyFormat(value: String): String = s"£$value"

  def escape(x: String): Html = HtmlFormat.escape(x)

  def shareCompName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(ShareCompanyNamePage(index)).getOrElse("")
  }

  def assetName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(BusinessNamePage(index)).getOrElse("")
  }

  def agencyName(userAnswers: UserAnswers): String = {
    userAnswers.get(AgentNamePage).getOrElse("")
  }

  def ukAddress(address: UKAddress): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        address.line4.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(address.postcode))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def internationalAddress(address: InternationalAddress, countryOptions: CountryOptions): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        Some(country(address.country, countryOptions))
      ).flatten

    Html(lines.mkString("<br />"))
  }

}
