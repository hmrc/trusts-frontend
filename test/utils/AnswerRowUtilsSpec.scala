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

package utils

import base.RegistrationSpecBase
import models.RegistrationSubmission.{AnswerRow, AnswerSection}
import play.api.i18n.{DefaultMessagesApi, Lang, MessagesImpl}

class AnswerRowUtilsSpec extends RegistrationSpecBase {

  val util: AnswerRowUtils = injector.instanceOf[AnswerRowUtils]

  "AnswerRowUtils" must {

    "reverse engineer answer" when {

      "answer is a date" when {

        "saved in English and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("3 February 1996", "3 February 1996"),
            ("13 March 2020", "13 March 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in English and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("3 February 1996", "3 Chwefror 1996"),
            ("13 March 2020", "13 Mawrth 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("3 Chwefror 1996", "3 February 1996"),
            ("13 Mawrth 2020", "13 March 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("3 Chwefror 1996", "3 Chwefror 1996"),
            ("13 Mawrth 2020", "13 Mawrth 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }
      }

      "answer is yes or no" when {

        "saved in English and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("Yes", "Yes"),
            ("No", "No")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in English and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("Yes", "Iawn"),
            ("No", "Na")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("Iawn", "Yes"),
            ("Na", "No")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("Iawn", "Iawn"),
            ("Na", "Na")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }
      }

      "answer is an enum" when {

        "saved in English and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("Individual", "Individual"),
            ("Business", "Business")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in English and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("Individual", "Unigolyn"),
            ("Business", "Busnes")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("Unigolyn", "Individual"),
            ("Busnes", "Business")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("Unigolyn", "Unigolyn"),
            ("Busnes", "Busnes")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }
      }

      "answer is a country" when {

        "saved in English and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("France", "France"),
            ("Germany", "Germany")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in English and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("France", "Ffrainc"),
            ("Germany", "Yr Almaen")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("Ffrainc", "France"),
            ("Yr Almaen", "Germany")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("Ffrainc", "Ffrainc"),
            ("Yr Almaen", "Yr Almaen")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "only return exact matches for countries" in {
          val inputsAndOutputs = Seq(
            ("Norfolk", "Norfolk"),
            ("F", "F"),
            ("Bos", "Bos")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }

        }
      }

      "answer is an international address" when {

        "saved in English and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("Line 1<br />Line 2<br />Line 3<br />France", "Line 1<br />Line 2<br />Line 3<br />France"),
            ("Line 1<br />Line 2<br />Line 3<br />Germany", "Line 1<br />Line 2<br />Line 3<br />Germany")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in English and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("Line 1<br />Line 2<br />Line 3<br />France", "Line 1<br />Line 2<br />Line 3<br />Ffrainc"),
            ("Line 1<br />Line 2<br />Line 3<br />Germany", "Line 1<br />Line 2<br />Line 3<br />Yr Almaen")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("Line 1<br />Line 2<br />Line 3<br />Ffrainc", "Line 1<br />Line 2<br />Line 3<br />France"),
            ("Line 1<br />Line 2<br />Line 3<br />Yr Almaen", "Line 1<br />Line 2<br />Line 3<br />Germany")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("Line 1<br />Line 2<br />Line 3<br />Ffrainc", "Line 1<br />Line 2<br />Line 3<br />Ffrainc"),
            ("Line 1<br />Line 2<br />Line 3<br />Yr Almaen", "Line 1<br />Line 2<br />Line 3<br />Yr Almaen")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }
      }

      "answer is passport/ID card details" when {

        "saved in English and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("France<br />1234567890<br />3 February 1996", "France<br />1234567890<br />3 February 1996"),
            ("Germany<br />1234567890<br />13 March 2020", "Germany<br />1234567890<br />13 March 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in English and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("France<br />1234567890<br />3 February 1996", "Ffrainc<br />1234567890<br />3 Chwefror 1996"),
            ("Germany<br />1234567890<br />13 March 2020", "Yr Almaen<br />1234567890<br />13 Mawrth 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("Ffrainc<br />1234567890<br />3 Chwefror 1996", "France<br />1234567890<br />3 February 1996"),
            ("Yr Almaen<br />1234567890<br />13 Mawrth 2020", "Germany<br />1234567890<br />13 March 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("Ffrainc<br />1234567890<br />3 Chwefror 1996", "Ffrainc<br />1234567890<br />3 Chwefror 1996"),
            ("Yr Almaen<br />1234567890<br />13 Mawrth 2020", "Yr Almaen<br />1234567890<br />13 Mawrth 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }
      }

      "answer is something else" must {
        "display original answer" in {

          val inputsAndOutputs = Seq(
            ("Google", "Google"),
            ("John Smith", "John Smith"),
            ("£1000", "£1000"),
            (
              "Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB1 1AB",
              "Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB1 1AB"
            )
          )

          Seq(ENGLISH, WELSH).foreach { language =>
            val messages: MessagesImpl = MessagesImpl(Lang(language), messagesApi)

            inputsAndOutputs.foreach { inputAndOutput =>
              val result = util.reverseEngineerAnswer(inputAndOutput._1)(messages)
              result mustEqual inputAndOutput._2
            }
          }
        }
      }
    }

    "reverse engineer arg" when {

      "arg is a date" when {

        "saved in English and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("3 February 1996", "3 February 1996"),
            ("13 March 2020", "13 March 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerArg(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in English and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("3 February 1996", "3 Chwefror 1996"),
            ("13 March 2020", "13 Mawrth 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerArg(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in English" in {

          val inputsAndOutputs = Seq(
            ("3 Chwefror 1996", "3 February 1996"),
            ("13 Mawrth 2020", "13 March 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(ENGLISH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerArg(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }

        "saved in Welsh and displayed in Welsh" in {

          val inputsAndOutputs = Seq(
            ("3 Chwefror 1996", "3 Chwefror 1996"),
            ("13 Mawrth 2020", "13 Mawrth 2020")
          )

          val messages: MessagesImpl = MessagesImpl(Lang(WELSH), messagesApi)

          inputsAndOutputs.foreach { inputAndOutput =>
            val result = util.reverseEngineerArg(inputAndOutput._1)(messages)
            result mustEqual inputAndOutput._2
          }
        }
      }

      "arg is something else" must {
        "display original arg" in {

          val inputsAndOutputs = Seq(
            ("Google", "Google"),
            ("John Smith", "John Smith"),
            ("£1000", "£1000"),
            (
              "Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB1 1AB",
              "Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB1 1AB"
            )
          )

          Seq(ENGLISH, WELSH).foreach { language =>
            val messages: MessagesImpl = MessagesImpl(Lang(language), messagesApi)

            inputsAndOutputs.foreach { inputAndOutput =>
              val result = util.reverseEngineerArg(inputAndOutput._1)(messages)
              result mustEqual inputAndOutput._2
            }
          }
        }
      }
    }

    "rowsWithCorrectTense return rows with the correct tense" when {

      val messagesApiWithPastTense = new DefaultMessagesApi(
        messages = Map(
          ENGLISH -> Map(
            "test.checkYourAnswersLabel"          -> "",
            "testPastTense.checkYourAnswersLabel" -> "",
            "settlor1.checkYourAnswersLabel"      -> "",
            "settlor2.checkYourAnswersLabel"      -> "",
            "settlor3.checkYourAnswersLabel"      -> "",
            "settlor4.checkYourAnswersLabel"      -> "",
            "settlor5.checkYourAnswersLabel"      -> ""
          ),
          WELSH   -> Map(
            "test.checkYourAnswersLabel"          -> "",
            "testPastTense.checkYourAnswersLabel" -> "",
            "settlor1.checkYourAnswersLabel"      -> "",
            "settlor2.checkYourAnswersLabel"      -> "",
            "settlor3.checkYourAnswersLabel"      -> "",
            "settlor4.checkYourAnswersLabel"      -> "",
            "settlor5.checkYourAnswersLabel"      -> ""
          )
        )
      )

      Seq(ENGLISH, WELSH).foreach(language =>
        s"there are no messages with a past tense suffix it should return the rows unchanged (when using $language)" in {

          val languageWithPastTense = MessagesImpl(Lang(language), messagesApiWithPastTense)

          val rows = Seq(
            AnswerRow(label = s"settlor1.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty),
            AnswerRow(label = s"settlor2.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty),
            AnswerRow(label = s"settlor3.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty),
            AnswerRow(label = s"settlor4.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty),
            AnswerRow(label = s"settlor5.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty)
          )

          val section = AnswerSection(headingKey = None, rows = rows, sectionKey = None, headingArgs = Seq.empty)
          util.rowsWithCorrectTense(section = section)(languageWithPastTense) mustBe rows
        }
      )

      Seq(ENGLISH, WELSH).foreach(language =>
        s"there are messages with the past tense suffix it should return those rows (when using $language)" in {

          val languageWithPastTense = MessagesImpl(Lang(language), messagesApiWithPastTense)

          val rows = Seq(
            AnswerRow(label = "test.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty),
            AnswerRow(label = "settlor2.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty)
          )

          val section = AnswerSection(headingKey = None, rows = rows, sectionKey = None, headingArgs = Seq.empty)

          util.rowsWithCorrectTense(section = section)(languageWithPastTense) mustBe Seq(
            AnswerRow(label = "testPastTense.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty),
            AnswerRow(label = "settlor2.checkYourAnswersLabel", answer = "text", labelArgs = Seq.empty)
          )
        }
      )

    }
  }

}
