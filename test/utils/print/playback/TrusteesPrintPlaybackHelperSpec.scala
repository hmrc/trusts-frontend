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
import pages.register.trustees._
import play.twirl.api.Html

class TrusteesPrintPlaybackHelperSpec extends PlaybackSpecBase with AnswerSectionMatchers with UserAnswersWriting {

  "when the lead trustee is an UK individual" must {
    "generate lead trustee section" in {
      val helper = injector.instanceOf[PrintPlaybackHelper]

      val (answers, _) = (for {
        _ <- individualUKTrustee(0)
        _ <- TrusteesNamePage(0) is FullName("Wild", Some("Bill"), "Hickock")
        _ <- TrusteesDateOfBirthPage(0) is LocalDate.parse("1975-01-23")
        _ <- TrusteesNinoPage(0) is "AA111111A"
        _ <- TrusteesUkAddressPage(0) is UKAddress("Address 1", "Address 2", None, None, "AA11 1AA")
        _ <- TelephoneNumberPage(0) is "67676767676"
        _ <- EmailPage(0) is "aa@aabb.com"
        _ <- individualNonUkTrustee(1)
        _ <- IsThisLeadTrusteePage(0) is true
      } yield Unit).run(emptyUserAnswers).value

      val result = helper.summary(answers)

      result must containHeadingSection("answerPage.section.trustees.heading")
      result must containSectionWithHeadingAndValues("answerPage.section.leadTrusteeIndividual.heading",
        "trusteeIndividualOrBusiness.checkYourAnswersLabel" -> Html("Individual"),
        "leadTrusteesName.checkYourAnswersLabel" -> Html("Wild Bill Hickock"),
        "What is Wild Bill Hickock’s date of birth?" -> Html("23 January 1975"),
        "What is Wild Bill Hickock’s National Insurance number?" -> Html("AA111111A"),
        "Does Wild Bill Hickock live in the Uk?" -> Html("Yes"),
        "What is Wild Bill Hickock’s address?" -> Html("Address 1<br />Address 2<br />AA11 1AA"),
        "What is Wild Bill Hickock’s telephone number?" -> Html("67676767676"),
        "What is Wild Bill Hickock’s email address?" -> Html("aa@aabb.com")
      )
    }
  }

  "when the lead trustee is a non-UK individual" must {
    "generate a lead trustee section" ignore {
      val helper = injector.instanceOf[PrintPlaybackHelper]

      val (answers, _) = (for {
        _ <- individualUKTrustee(0)
        _ <- individualNonUkTrustee(1)
        _ <- TrusteesNamePage(1) is FullName("William", None, "Bonny")
        _ <- IsThisLeadTrusteePage(1) is true
      } yield Unit).run(emptyUserAnswers).value

      val result = helper.summary(answers)

      result must containHeadingSection("answerPage.section.trustees.heading")
      result must containSectionWithHeadingAndValues("answerPage.section.leadTrusteeIndividual.heading",
        "trusteeName.checkYourAnswersLabel" -> Html("William Bonny")
      )
    }
  }

  "when the lead trustee is a company" must {
    "generate a lead trustee section" ignore {}
  }

  "when there is an organisation trustee" must {
    "generate a trustee section for each trustee" ignore {}
  }
}
