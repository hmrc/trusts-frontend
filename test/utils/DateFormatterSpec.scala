/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.{LocalDateTime, LocalDate => JavaDate}

import base.RegistrationSpecBase
import config.FrontendAppConfig
import org.joda.time.{LocalDate => JodaDate}
import org.mockito.Mockito.when
import play.api.i18n.{Lang, MessagesImpl}
import uk.gov.hmrc.play.language.LanguageUtils

class DateFormatterSpec extends RegistrationSpecBase {

  private val mockConfig: FrontendAppConfig = mock[FrontendAppConfig]
  when(mockConfig.ttlInSeconds).thenReturn(60*60*24*28)

  private val languageUtils: LanguageUtils = injector.instanceOf[LanguageUtils]

  private val formatter: DateFormatter = new DateFormatter(mockConfig, languageUtils)

  private def messages(langCode: String): MessagesImpl = {
    val lang: Lang = Lang(langCode)
    MessagesImpl(lang, messagesApi)
  }

  private val day: Int = 3
  private val month: Int = 2
  private val year: Int = 1996

  private val dateTime: LocalDateTime = LocalDateTime.of(year, month, day, 0, 0)
  private val javaDate: JavaDate = JavaDate.of(year, month, day)
  private val jodaDate: JodaDate = JodaDate.parse(s"$year-$month-$day")

  "DateFormatter" when {

    ".savedUntil" when {

      "in English mode" must {
        "format date in English" in {
          val result: String = formatter.savedUntil(dateTime)(messages("en"))
          result mustBe "2 March 1996"
        }
      }

      "in Welsh mode" must {
        "format date in Welsh" in {
          val result: String = formatter.savedUntil(dateTime)(messages("cy"))
          result mustBe "2 Mawrth 1996"
        }
      }
    }

    ".formatDateTime" when {

      "in English mode" must {
        "format date in English" in {

          val result: String = formatter.formatDateTime(dateTime)(messages("en"))
          result mustBe "3 February 1996"
        }
      }

      "in Welsh mode" must {
        "format date in Welsh" in {

          val result: String = formatter.formatDateTime(dateTime)(messages("cy"))
          result mustBe "3 Chwefror 1996"
        }
      }
    }

    ".formatDate (Java)" when {

      "in English mode" must {
        "format date in English" in {

          val result: String = formatter.formatDate(javaDate)(messages("en"))
          result mustBe "3 February 1996"
        }
      }

      "in Welsh mode" must {
        "format date in Welsh" in {

          val result: String = formatter.formatDate(javaDate)(messages("cy"))
          result mustBe "3 Chwefror 1996"
        }
      }
    }

    ".formatDate (Joda)" when {

      "in English mode" must {
        "format date in English" in {

          val result: String = formatter.formatDate(jodaDate)(messages("en"))
          result mustBe "3 February 1996"
        }
      }

      "in Welsh mode" must {
        "format date in Welsh" in {

          val result: String = formatter.formatDate(jodaDate)(messages("cy"))
          result mustBe "3 Chwefror 1996"
        }
      }
    }
  }

}
