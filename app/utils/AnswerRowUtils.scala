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

import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.ULocale
import mapping.Constants._
import play.api.Environment
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.play.language.LanguageUtils

import java.io.{BufferedReader, InputStreamReader}
import java.time.ZoneId
import javax.inject.Inject
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.util.{Failure, Success, Try}

class AnswerRowUtils @Inject()(languageUtils: LanguageUtils,
                               messagesApi: MessagesApi,
                               environment: Environment) {

  private val languages = Seq(ENGLISH, WELSH)

  def reverseEngineerAnswer(answer: String)(implicit messages: Messages): String = {
    parseAsYesOrNo(answer) orElse
      parseAsDate(answer) orElse
      parseAsEnumerable(answer) orElse
      parseAsCountry(answer) getOrElse
      answer
  }

  private def parseAsYesOrNo(answer: String)(implicit messages: Messages): Try[String] = {
    val keys = Seq("site.yes", "site.no")
    findAnswerInMessages(answer, Some(keys))
  }

  private def parseAsEnumerable(answer: String)(implicit messages: Messages): Try[String] = {
    findAnswerInMessages(answer)
  }

  private def findAnswerInMessages(answer: String, messageKeys: Option[Seq[String]] = None)
                                  (implicit messages: Messages): Try[String] = {
    languages.foldLeft[Try[String]](Failure(new IllegalArgumentException()))((acc1, language) => {
      val messagesForLanguage = MessagesImpl(Lang(language), messagesApi)

      messageKeys.getOrElse(getMessageKeysForLanguage(language)).foldLeft(acc1)((acc2, key) => {
        if (answer == messagesForLanguage(key)) {
          Success(messages(key))
        } else {
          acc2
        }
      })
    })
  }

  private def parseAsDate(answer: String)(implicit messages: Messages): Try[String] = {
    languages.foldLeft[Try[String]](Failure(new IllegalArgumentException()))((acc, language) => {
      val format = new SimpleDateFormat("d MMMM y", new ULocale(language))

      Try(format.parse(answer))
        .map(x => x.toInstant.atZone(ZoneId.systemDefault()).toLocalDate)
        .map(date => languageUtils.Dates.formatDate(date))
        .orElse(acc)
    })
  }

  private def parseAsCountry(answer: String)(implicit messages: Messages): Try[String] = {
    languages.foldLeft[Try[String]](Failure(new IllegalArgumentException()))((acc, language) => {
      val countryCode: Option[String] = getCountriesForLanguage(language).find(_.exists(_.contains(answer))) map {
        _.last.split("country:").last
      }

      countryCode match {
        case Some(cc) => getCountriesForLanguage(messages.lang.code).find(x => x.last.contains(cc)) match {
          case Some(country) => Success(country.head)
          case _ => acc
        }
        case _ => acc
      }
    })
  }

  private def getMessageKeysForLanguage(language: String): Seq[String] = {
    val fileName = s"messages.$language"

    environment.resourceAsStream(fileName).fold[Seq[String]](Nil)(inputStream => {
      val reader = new BufferedReader(new InputStreamReader(inputStream))
      reader.lines().iterator().asScala.map(x => x.split("=").head.trim).toSeq
    })
  }

  private def getCountriesForLanguage(language: String): Seq[Seq[String]] = {
    environment.resourceAsStream(getCountryListForLanguage(language)) match {
      case Some(value) => Json.fromJson[Seq[Seq[String]]](Json.parse(value)) match {
        case JsSuccess(value, _) => value
        case _ => Nil
      }
      case _ => Nil
    }
  }

  private def getCountryListForLanguage(language: String): String = {
    if (language == ENGLISH) {
      "location-autocomplete-canonical-list.json"
    } else {
      "location-autocomplete-canonical-list-cy.json"
    }
  }

}
