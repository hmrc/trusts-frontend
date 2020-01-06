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

import base.PlaybackSpecBase
import models.core.pages.FullName
import pages.register.protectors.company.CompanyProtectorNamePage
import pages.register.protectors.individual.IndividualProtectorNamePage
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class ProtectorsPrintPlaybackHelperSpec extends PlaybackSpecBase {

  "Playback print helper" must {

    "generate protector sections" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
          .set(IndividualProtectorNamePage(0), FullName("Michael", None, "Finnegan")).success.value
          .set(IndividualProtectorNamePage(1), FullName("Joe", None, "Bloggs")).success.value
          .set(CompanyProtectorNamePage(0), "Bernardos").success.value
          .set(CompanyProtectorNamePage(1), "Red Cross Ltd.").success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.protectors.heading")),
        AnswerSection(
          headingKey = Some("Individual protector 1"),
          rows = Seq(
            AnswerRow(label = "individualProtectorName.checkYourAnswersLabel", answer = Html("Michael Finnegan"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Individual protector 2"),
          rows = Seq(
            AnswerRow(label = "individualProtectorName.checkYourAnswersLabel", answer = Html("Joe Bloggs"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Company protector 1"),
          rows = Seq(
            AnswerRow(label = "companyProtectorName.checkYourAnswersLabel", answer = Html("Bernardos"), changeUrl = None)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Company protector 2"),
          rows = Seq(
            AnswerRow(label = "companyProtectorName.checkYourAnswersLabel", answer = Html("Red Cross Ltd."), changeUrl = None)
          ),
          sectionKey = None
        )
      )
    }
  }
}
