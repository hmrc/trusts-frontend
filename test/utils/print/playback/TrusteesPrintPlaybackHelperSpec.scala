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
import models.core.pages.{FullName, IndividualOrBusiness}
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage, TrusteesNamePage}
import play.twirl.api.Html
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class TrusteesPrintPlaybackHelperSpec extends PlaybackSpecBase {

  "Playback print helper" must {

    "generate trustee sections given individual lead trustee" in {

      val answers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(0), true).success.value
        .set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
        .set(TrusteesNamePage(0), FullName("Joe", None, "Bloggs")).success.value

      val helper = new PlaybackAnswersHelper(countryOptions = injector.instanceOf[CountryOptions], userAnswers = answers)

      val result = helper.allTrustees

      val name1 = "Joe Bloggs"

      result mustBe Seq(
        AnswerSection(
          headingKey = Some("Trustee 1"),
          rows = Seq(
            AnswerRow(label = messages("isThisLeadTrustee.checkYourAnswersLabel"), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("trusteeIndividualOrBusiness.checkYourAnswersLabel", name1), answer = Html("Individual"), changeUrl = None),
            AnswerRow(label = messages("trusteesName.checkYourAnswersLabel", name1), answer = Html("Joe Bolggs"), changeUrl = None)
          ),
          sectionKey = Some("Trustees")
        )
      )
    }
  }
}





