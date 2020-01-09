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

package utils.print.playback

import java.time.LocalDate

import base.PlaybackSpecBase
import models.core.pages.FullName
import pages.register.settlors.deceased_settlor._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class SettlorsPrintPlaybackHelperSpec extends PlaybackSpecBase {

  "Settlors print playback helper" must {

    "generate deceased settlor sections" in {

      val name = "Adam Smith"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(SettlorsNamePage, FullName("Adam", None, "Smith")).success.value
        .set(SettlorDateOfDeathYesNoPage, true).success.value
        .set(SettlorDateOfDeathPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.of(1991, 8, 27)).success.value
        .set(SettlorsNationalInsuranceYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "JP121212A").success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.deceasedSettlor.heading")),
        AnswerSection(
          headingKey = Some("Settlor"),
          rows = Seq(
            AnswerRow(label = "settlorsName.checkYourAnswersLabel", answer = Html("Adam Smith"), changeUrl = None),
            AnswerRow(label = "settlorDateOfDeathYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = name),
            AnswerRow(label = "settlorDateOfDeath.checkYourAnswersLabel", answer = Html("10 October 2010"), changeUrl = None, labelArg = name),
            AnswerRow(label = "settlorDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = name),
            AnswerRow(label = "settlorsDateOfBirth.checkYourAnswersLabel", answer = Html("27 August 1991"), changeUrl = None, labelArg = name),
            AnswerRow(label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = name),
            AnswerRow(label = "settlorNationalInsuranceNumber.checkYourAnswersLabel", answer = Html("JP 12 12 12 A"), changeUrl = None, labelArg = name)
          ),
          sectionKey = None
        )
      )

    }

  }

}