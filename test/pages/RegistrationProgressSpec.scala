/*
 * Copyright 2019 HM Revenue & Customs
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

package pages

import java.time.LocalDate

import base.SpecBase
import models.AddATrustee
import viewmodels.Tag
import viewmodels.Tag.Completed

class RegistrationProgressSpec extends SpecBase {

  "Trust details section" must {

    "render no tag" when {

      "no status value in user answers" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers

        registrationProgress.isTrustDetailsComplete(userAnswers) mustBe false
      }

    }

    "render in-progress tag" when {

      "user has entered when the trust was created" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value

        registrationProgress.isTrustDetailsComplete(userAnswers) mustBe false
      }

    }

    "render complete tag" when {

      "user answer has reached check-trust-details" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
          .set(TrustDetailsCompleted, Completed).success.value

        registrationProgress.isTrustDetailsComplete(userAnswers) mustBe true
      }

    }

  }


  "Trustee section" must {

    "render no tag" when {

      "no trustees in user answers" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers

        registrationProgress.isTrusteesComplete(userAnswers) mustBe false
      }

    }

    "render in-progress tag" when {

      "there are trustees that are incomplete" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(0), true).success.value
          .set(IsThisLeadTrusteePage(1), false).success.value
          .set(TrusteeComplete(1), Tag.Completed).success.value

        registrationProgress.isTrusteesComplete(userAnswers) mustBe false
      }

      "there are trustees that are complete, but section flagged not complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(0), true).success.value
          .set(TrusteeComplete(0), Tag.Completed).success.value
          .set(IsThisLeadTrusteePage(1), false).success.value
          .set(TrusteeComplete(1), Tag.Completed).success.value
          .set(AddATrusteePage, AddATrustee.YesLater).success.value

        registrationProgress.isTrusteesComplete(userAnswers) mustBe false
      }

      "there are completed trustees, the section is flagged as completed, but there is no lead trustee" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(0), false).success.value
          .set(TrusteeComplete(0), Tag.Completed).success.value
          .set(IsThisLeadTrusteePage(1), false).success.value
          .set(TrusteeComplete(1), Tag.Completed).success.value
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        registrationProgress.isTrusteesComplete(userAnswers) mustBe false
      }

    }

    "render complete tag" when {

      "there are trustees that are complete, and section flagged as complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(0), true).success.value
          .set(TrusteeComplete(0), Tag.Completed).success.value
          .set(IsThisLeadTrusteePage(1), false).success.value
          .set(TrusteeComplete(1), Tag.Completed).success.value
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        registrationProgress.isTrusteesComplete(userAnswers) mustBe true
      }

    }

  }

  "Settlor section" must {

    "render no tag" when {

      "no deceased settlor in user answers" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers

        registrationProgress.isDeceasedSettlorComplete(userAnswers) mustBe false
      }

    }

    "render in-progress tag" when {

      "there is a deceased settlor that is not completed" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
            .set(SetupAfterSettlorDiedPage, true).success.value
            .set(DeceasedSettlorComplete, Tag.InProgress).success.value

        registrationProgress.isDeceasedSettlorComplete(userAnswers) mustBe false
      }

    }

    "render complete tag" when {

      "there is a deceased settlor marked as complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, true).success.value
          .set(DeceasedSettlorComplete, Tag.Completed).success.value

        registrationProgress.isDeceasedSettlorComplete(userAnswers) mustBe true
      }

    }

  }

}
