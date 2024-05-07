/*
 * Copyright 2024 HM Revenue & Customs
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

package views

import play.api.data.{Field, Form, FormError}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.{RadioItem, Text, Hint}
import viewmodels.RadioOption

object ViewUtils {

  def errorPrefix(form: Form[_])(implicit messages: Messages): String = {
    if (form.hasErrors || form.hasGlobalErrors) s"${messages("error.browser.title.prefix")} " else ""
  }

  def breadcrumbTitle(title: String, section: Option[String] = None)(implicit messages: Messages): String = {
    section match {
      case Some(sect) => s"$title - $sect - ${messages("service.name")} - GOV.UK"
      case _ => s"$title - ${messages("service.name")} - GOV.UK"
    }
  }

  def errorHref(error: FormError, radioOptions: Seq[RadioOption] = Nil, isYesNo: Boolean = false): String = {
    error.args match {
      case x if x.contains("day") || x.contains("month") || x.contains("year") =>
        s"${error.key}.${error.args.head}"
      case _ if isYesNo =>
        s"${error.key}-yes"
      case _ if radioOptions.size != 0 =>
        radioOptions.head.id
      case _ =>
        val isSingleDateField = error.message.toLowerCase.contains("date") && !error.message.toLowerCase.contains("yesno")
        if (error.key.toLowerCase.contains("date") || isSingleDateField) {
          s"${error.key}.day"
        } else {
          s"${error.key}"
        }
    }
  }

  def mapRadioOptionsToRadioItems(field: Field, trackGa: Boolean,
                                  inputs: Seq[(RadioOption, String)])(implicit messages: Messages): Seq[RadioItem] =
    inputs.map {
      input =>
        val (item, hint) = input
        RadioItem(
          id = Some(item.id),
          value = Some(item.value),
          checked = field.value.contains(item.value),
          content = Text(messages(item.messageKey)),
          hint = if (hint.nonEmpty) Some(Hint(content = Text(messages(hint)))) else None,
          attributes = if (trackGa) Map[String, String]("data-journey-click" -> s"trusts-frontend:click:${item.id}") else Map.empty
        )
    }
}
