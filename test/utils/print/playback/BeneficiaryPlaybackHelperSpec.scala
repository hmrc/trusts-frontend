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
import models.core.pages.{FullName, UKAddress}
import pages.register.beneficiaries.charity._
import pages.register.beneficiaries.individual.IndividualBeneficiaryNamePage
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class BeneficiaryPlaybackHelperSpec extends PlaybackSpecBase {

  "Playback print helper" must {

    "generate charity beneficiaries sections" in {

      val charityBen1Name = "Red Cross Ltd."
      val charityBen2Name = "Bernardos"

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(CharityBeneficiaryNamePage(0), charityBen1Name).success.value
        .set(CharityBeneficiaryDiscretionYesNoPage(0), false).success.value
        .set(CharityBeneficiaryShareOfIncomePage(0), "98").success.value
        .set(CharityBeneficiaryAddressYesNoPage(0), true).success.value
        .set(CharityBeneficiaryAddressUKYesNoPage(0), true).success.value
        .set(CharityBeneficiaryAddressPage(0),
          UKAddress(
            line1 = "line1",
            line2 = "line2",
            line3 = Some("line3"),
            line4 = Some("line4"),
            postcode = "NE981ZZ"
          )
        ).success.value

        .set(CharityBeneficiaryNamePage(1), charityBen2Name).success.value
        .set(CharityBeneficiaryDiscretionYesNoPage(1), true).success.value
        .set(CharityBeneficiaryAddressYesNoPage(1), false).success.value
        .set(CharityBeneficiaryUtrPage(1), "1234567890").success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Charity beneficiary 1"),
          rows = Seq(
            AnswerRow(label = "charityBeneficiaryName.checkYourAnswersLabel", answer = Html("Red Cross Ltd."), changeUrl = None),
            AnswerRow(label = "charityBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = None, labelArg = charityBen1Name),
            AnswerRow(label = "charityBeneficiaryShareOfIncome.checkYourAnswersLabel", answer = Html("98"), changeUrl = None, labelArg = charityBen1Name),
            AnswerRow(label = "charityBeneficiaryAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = charityBen1Name),
            AnswerRow(label = "charityBeneficiaryAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = charityBen1Name),
            AnswerRow(label = "charityBeneficiaryAddress.checkYourAnswersLabel", answer = Html("line1<br />line2<br />line3<br />line4<br />NE981ZZ"), changeUrl = None, labelArg = charityBen1Name)
          ),
          sectionKey = None
        ),
        AnswerSection(
          headingKey = Some("Charity beneficiary 2"),
          rows = Seq(
            AnswerRow(label = "charityBeneficiaryName.checkYourAnswersLabel", answer = Html("Bernardos"), changeUrl = None),
            AnswerRow(label = "charityBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = None, labelArg = charityBen2Name),
            AnswerRow(label = "charityBeneficiaryAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = None, labelArg = charityBen2Name),
            AnswerRow(label = "charityBeneficiaryUtr.checkYourAnswersLabel", answer = Html("1234567890"), changeUrl = None, labelArg = charityBen2Name)
          ),
          sectionKey = None
        )
      )

    }

    "generate individual beneficiaries sections" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(IndividualBeneficiaryNamePage(0), FullName("Michael", None, "Finnegan")).success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(None, Nil, Some("answerPage.section.beneficiaries.heading")),
        AnswerSection(
          headingKey = Some("Individual beneficiary 1"),
          rows = Seq(
            AnswerRow(label = "What is the name of the individual?", answer = Html("Michael Finnegan"), changeUrl = None)
          ),
          sectionKey = None
        )
      )

    }

  }

}