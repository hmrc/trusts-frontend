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
import pages.register.beneficiaries.charity._
import pages.register.beneficiaries.individual.IndividualBeneficiaryNamePage
import play.twirl.api.Html

class PrintPlaybackHelperSpec extends PlaybackSpecBase with AnswerSectionMatchers {

  "Playback print helper" must {

    "generate beneficiary sections" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
          .set(CharityBeneficiaryNamePage(0), "Red Cross Ltd.").success.value
          .set(CharityBeneficiaryNamePage(1), "Bernardos").success.value
          .set(IndividualBeneficiaryNamePage(0), FullName("Michael", None, "Finnegan")).success.value

      val result = helper.summary(answers)

      result must containHeadingSection("answerPage.section.beneficiaries.heading")
      result must containSectionWithHeadingAndValues("Individual beneficiary 1",
        "What is the name of the individual?" -> Html("Michael Finnegan")
      )
      result must containSectionWithHeadingAndValues("Charity beneficiary 1",
        "charityBeneficiaryName.checkYourAnswersLabel" -> Html("Red Cross Ltd.")
      )
      result must containSectionWithHeadingAndValues("Charity beneficiary 2",
        "charityBeneficiaryName.checkYourAnswersLabel" -> Html("Bernardos")
      )
    }
  }

}
