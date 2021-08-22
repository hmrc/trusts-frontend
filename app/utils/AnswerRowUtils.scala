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
import scala.annotation.tailrec
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.util.{Failure, Success, Try}

class AnswerRowUtils @Inject()(languageUtils: LanguageUtils,
                               messagesApi: MessagesApi,
                               environment: Environment) {

  private val languages = Seq(ENGLISH, WELSH)
  private val lineBreak = "<br />"

  def reverseEngineerAnswer(answer: String)(implicit messages: Messages): String = {
    answer.split(lineBreak).map(line =>
      parseAsYesOrNo(line) orElse
        parseAsMaskedIdNumber(line) orElse
        parseAsDate(line) orElse
        parseAsEnumerable(line) orElse
        parseAsCountry(line) getOrElse
        line
    ).mkString(lineBreak)
  }

  def reverseEngineerArg(arg: String)(implicit messages: Messages): String = {
    parseAsDate(arg) getOrElse
      arg
  }

  private def parseAsYesOrNo(answer: String)(implicit messages: Messages): Try[String] = {
    val keys = Seq("site.yes", "site.no")
    findAnswerInMessages(answer, Some(keys))
  }

  private def parseAsEnumerable(answer: String)(implicit messages: Messages): Try[String] = {
    findAnswerInMessages(answer)
  }

  private def parseAsMaskedIdNumber(answer: String)(implicit messages: Messages): Try[String] = {
    val keys = Seq("site.number-ending")
    findAnswerInMessages(answer, Some(keys))
  }

  private def findAnswerInMessages(answer: String, messageKeys: Option[Seq[String]] = None)
                                  (implicit messages: Messages): Try[String] = {

    def findAnswerInMessages(languages: Seq[String]): Try[String] = {
      languages match {
        case Nil => Failure(new IllegalArgumentException())
        case _ =>
          val language = languages.head
          findAnswerInMessagesForLanguage(messageKeys.getOrElse(getMessageKeysForLanguage(language)), language)
            .orElse(findAnswerInMessages(languages.tail))
      }
    }

    @tailrec
    def findAnswerInMessagesForLanguage(messageKeys: Seq[String], language: String): Try[String] = {
      val messagesForLanguage = MessagesImpl(Lang(language), messagesApi)
      messageKeys match {
        case Nil => Failure(new IllegalArgumentException())
        case _ =>
          val key = messageKeys.head
          if (answer == messagesForLanguage(key)) {
            Success(messages(key))
          } else {
            val pattern = messagesForLanguage(key, "(.+)").r
            if (answer.matches(pattern.toString())) {
              val pattern(arg) = answer
              Success(messages(key, arg))
            } else {
              findAnswerInMessagesForLanguage(messageKeys.tail, language)
            }
          }
      }
    }

    findAnswerInMessages(languages)
  }

  private def parseAsDate(answer: String)(implicit messages: Messages): Try[String] = {

    def parseAsDate(languages: Seq[String]): Try[String] = {
      languages match {
        case Nil => Failure(new IllegalArgumentException())
        case _ =>
          val format = new SimpleDateFormat("d MMMM y", new ULocale(languages.head))
          Try(format.parse(answer))
            .map(_.toInstant.atZone(ZoneId.systemDefault()).toLocalDate)
            .map(languageUtils.Dates.formatDate)
            .orElse(parseAsDate(languages.tail))
      }
    }

    parseAsDate(languages)
  }

  private def parseAsCountry(answer: String)(implicit messages: Messages): Try[String] = {

    def parseAsCountry(languages: Seq[String]): Try[String] = {
      languages match {
        case Nil => Failure(new IllegalArgumentException())
        case _ =>
          val countryCode: Option[String] = getCountriesForLanguage(languages.head)
            .find(_.exists(_.contains(answer)))
            .map(_.last.split("country:").last)

          countryCode
            .flatMap(cc => getCountriesForLanguage(messages.lang.code).find(_.last.contains(cc)))
            .map(country => Success(country.head))
            .getOrElse(parseAsCountry(languages.tail))
      }
    }

    parseAsCountry(languages)
  }

  private def getMessageKeysForLanguage(language: String): Seq[String] = {
    val fileName = s"messages.$language"

    environment.resourceAsStream(fileName).fold[Seq[String]](Nil)(inputStream => {
      val reader = new BufferedReader(new InputStreamReader(inputStream))
      reader.lines().iterator().asScala.map(_.split("=").head.trim).toSeq
    })
  }

  private def getCountriesForLanguage(language: String): Seq[Seq[String]] = {

    type CountriesForLanguage = Seq[Seq[String]]

    environment.resourceAsStream(getCountryListForLanguage(language))
      .fold[CountriesForLanguage](Nil)(inputStream => {
        Json.fromJson[CountriesForLanguage](Json.parse(inputStream)) match {
          case JsSuccess(countriesForLanguage, _) => countriesForLanguage
          case _ => Nil
        }
      })
  }

  private def getCountryListForLanguage(language: String): String = {
    if (language == ENGLISH) {
      "location-autocomplete-canonical-list.json"
    } else {
      "location-autocomplete-canonical-list-cy.json"
    }
  }

}
