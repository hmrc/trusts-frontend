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

package models

import base.RegistrationSpecBase
import models.RegistrationSubmission.{AnswerRow, AnswerSection}
import play.api.libs.json.Json

class SubmissionDraftResponseSpec extends RegistrationSpecBase {

  "SubmissionDraftResponse" when {

    "AnswerRow" must {
      "read json" when {

        "old shape" when {

          "labelArg empty" in {

            val json = Json.parse(
              """
                |{
                |  "label": "Label",
                |  "answer": "Answer",
                |  "labelArg": ""
                |}
                |""".stripMargin)

            val result = json.as[AnswerRow]

            result mustEqual AnswerRow(
              label = "Label",
              answer = "Answer",
              labelArgs = Nil
            )
          }

          "labelArg not empty" in {

            val json = Json.parse(
              """
                |{
                |  "label": "Label",
                |  "answer": "Answer",
                |  "labelArg": "Label Arg"
                |}
                |""".stripMargin)

            val result = json.as[AnswerRow]

            result mustEqual AnswerRow(
              label = "Label",
              answer = "Answer",
              labelArgs = Seq("Label Arg")
            )
          }
        }

        "new shape" when {

          "labelArgs empty" in {

            val json = Json.parse(
              """
                |{
                |  "label": "Label",
                |  "answer": "Answer",
                |  "labelArgs": []
                |}
                |""".stripMargin)

            val result = json.as[AnswerRow]

            result mustEqual AnswerRow(
              label = "Label",
              answer = "Answer",
              labelArgs = Nil
            )
          }

          "labelArgs not empty" in {

            val json = Json.parse(
              """
                |{
                |  "label": "Label",
                |  "answer": "Answer",
                |  "labelArgs": [
                |    "Label Arg 1",
                |    "Label Arg 2"
                |  ]
                |}
                |""".stripMargin)

            val result = json.as[AnswerRow]

            result mustEqual AnswerRow(
              label = "Label",
              answer = "Answer",
              labelArgs = Seq("Label Arg 1", "Label Arg 2")
            )
          }
        }
      }
    }

    "AnswerSection" must {
      "read headingArgs with default empty list" in {

        val json = Json.parse(
          """
            |{
            |  "headingKey": "Heading Key",
            |  "rows": [
            |    {
            |      "label": "Label",
            |      "answer": "Answer",
            |      "labelArgs": []
            |    }
            |  ],
            |  "sectionKey": "Section Key"
            |}
            |""".stripMargin)

        val result = json.as[AnswerSection]

        result mustEqual AnswerSection(
          headingKey = Some("Heading Key"),
          rows = Seq(AnswerRow("Label", "Answer", Nil)),
          sectionKey = Some("Section Key"),
          headingArgs = Nil
        )
      }
    }
  }
}
