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
import models.core.pages.{FullName, UKAddress}
import models.playback.UserAnswers
import models.registration.pages.PassportOrIdCardDetails
import pages.register.TrustNamePage
import pages.register.settlors.deceased_settlor._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class SettlorsPrintPlaybackHelperSpec extends PlaybackSpecBase {

  "Settlors print playback helper" must {

    val answersWithTrustDetails: UserAnswers = emptyUserAnswers.set(TrustNamePage, "Trust Ltd.").success.value

    val trustDetails: AnswerSection = AnswerSection(
      headingKey = Some("answerPage.section.trustsDetails.heading"),
      rows = Seq(
        AnswerRow(label = "What is the trust’s name?", answer = Html("Trust Ltd."), changeUrl = None)
      )
    )

    "generate deceased settlor sections for maximum dataset" in {

      val name = "Adam Smith"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = answersWithTrustDetails
        .set(SettlorsNamePage, FullName("Adam", None, "Smith")).success.value
        .set(SettlorDateOfDeathYesNoPage, true).success.value
        .set(SettlorDateOfDeathPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.of(1991, 8, 27)).success.value
        .set(SettlorsNationalInsuranceYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "JP121212A").success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        trustDetails,
        AnswerSection(None, Nil, Some(messages("answerPage.section.deceasedSettlor.heading"))),
        AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("settlorsName.checkYourAnswersLabel"), answer = Html("Adam Smith"), changeUrl = None),
            AnswerRow(label = messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("settlorDateOfDeath.checkYourAnswersLabel", name), answer = Html("10 October 2010"), changeUrl = None),
            AnswerRow(label = messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("settlorsDateOfBirth.checkYourAnswersLabel", name), answer = Html("27 August 1991"), changeUrl = None),
            AnswerRow(label = messages("settlorsNationalInsuranceYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("settlorNationalInsuranceNumber.checkYourAnswersLabel", name), answer = Html("JP 12 12 12 A"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

    "generate deceased settlor sections for minimum dataset" in {

      val name = "Adam Smith"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = answersWithTrustDetails
        .set(SettlorsNamePage, FullName("Adam", None, "Smith")).success.value
        .set(SettlorDateOfDeathYesNoPage, false).success.value
        .set(SettlorDateOfBirthYesNoPage, false).success.value
        .set(SettlorsNationalInsuranceYesNoPage, false).success.value
        .set(SettlorsLastKnownAddressYesNoPage, true).success.value
        .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress(
          line1 = "line 1",
          line2 = "line 2",
          line3 = Some("line 3"),
          line4 = Some("line 4"),
          postcode = "NE981ZZ"
        )).success.value
        .set(SettlorsPassportIDCardPage,
          PassportOrIdCardDetails("DE", "123456789", LocalDate.of(2021,10,10))
        ).success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        trustDetails,
        AnswerSection(None, Nil, Some(messages("answerPage.section.deceasedSettlor.heading"))),
        AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("settlorsName.checkYourAnswersLabel"), answer = Html("Adam Smith"), changeUrl = None),
            AnswerRow(label = messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("settlorsNationalInsuranceYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = None),
            AnswerRow(label = messages("settlorsLastKnownAddressYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("wasSettlorsAddressUKYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("settlorsUKAddress.checkYourAnswersLabel", name), answer = Html("line 1<br />line 2<br />line 3<br />line 4<br />NE981ZZ"), changeUrl = None),
            AnswerRow(label = messages("settlorsPassportOrIdCard.checkYourAnswersLabel", name), answer = Html("Germany<br />123456789<br />10 October 2021"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

  }

}
