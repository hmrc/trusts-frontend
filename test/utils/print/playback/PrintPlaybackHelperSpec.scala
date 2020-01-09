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
import models.registration.pages.SettlorKindOfTrust
import pages.register.settlors.deceased_settlor.SetupAfterSettlorDiedPage
import pages.register.settlors.living_settlor.SettlorKindOfTrustPage
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class PrintPlaybackHelperSpec extends PlaybackSpecBase {

  "Playback print helper" must {

    "generate trust details for trust setup after settlor died" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, true).success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(
          headingKey = Some("Trust details"),
          rows = Seq(
            AnswerRow(label = messages("trustDetailsTrustType.checkYourAnswersLabel"),
              answer = Html("Will Trust or Intestacy Trust"),
              changeUrl = None
            )
          ),
          sectionKey = None
        )
      )

    }

    "generate trust details for trust through a deed of variation or family agreement" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(SetupAfterSettlorDiedPage, false).success.value
        .set(SettlorKindOfTrustPage, SettlorKindOfTrust.Deed).success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(
          headingKey = Some("Trust details"),
          rows = Seq(
            AnswerRow(label = messages("trustDetailsTrustType.checkYourAnswersLabel"),
              answer = Html("Deed of Variation Trust or Family Arrangement"),
              changeUrl = None
            )
          ),
          sectionKey = None
        )
      )

    }

    "generate trust details for trust created during their lifetime to gift or transfer assets" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(SetupAfterSettlorDiedPage, false).success.value
        .set(SettlorKindOfTrustPage, SettlorKindOfTrust.Intervivos).success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(
          headingKey = Some("Trust details"),
          rows = Seq(
            AnswerRow(label = messages("trustDetailsTrustType.checkYourAnswersLabel"),
              answer = Html("Inter vivos Settlement"),
              changeUrl = None
            )
          ),
          sectionKey = None
        )
      )

    }

    "generate trust details for trust for a building or building with tenants" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(SetupAfterSettlorDiedPage, false).success.value
        .set(SettlorKindOfTrustPage, SettlorKindOfTrust.FlatManagement).success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(
          headingKey = Some("Trust details"),
          rows = Seq(
            AnswerRow(label = messages("trustDetailsTrustType.checkYourAnswersLabel"),
              answer = Html("Flat Management Company or Sinking Fund"),
              changeUrl = None
            )
          ),
          sectionKey = None
        )
      )

    }

    "generate trust details for trust for the repair of historic buildings" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(SetupAfterSettlorDiedPage, false).success.value
        .set(SettlorKindOfTrustPage, SettlorKindOfTrust.HeritageMaintenanceFund).success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(
          headingKey = Some("Trust details"),
          rows = Seq(
            AnswerRow(label = messages("trustDetailsTrustType.checkYourAnswersLabel"),
              answer = Html("Heritage Maintenance Fund"),
              changeUrl = None
            )
          ),
          sectionKey = None
        )
      )

    }

    "generate trust details for trust for the employees of a company" in {

      val helper = injector.instanceOf[PrintPlaybackHelper]

      val answers = emptyUserAnswers
        .set(SetupAfterSettlorDiedPage, false).success.value
        .set(SettlorKindOfTrustPage, SettlorKindOfTrust.Employees).success.value

      val result = helper.summary(answers)

      result mustBe Seq(
        AnswerSection(
          headingKey = Some("Trust details"),
          rows = Seq(
            AnswerRow(label = messages("trustDetailsTrustType.checkYourAnswersLabel"),
              answer = Html("Employment Related"),
              changeUrl = None
            )
          ),
          sectionKey = None
        )
      )

    }

  }

}
