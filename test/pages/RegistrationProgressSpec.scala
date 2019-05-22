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

import base.SpecBase
import models.AddATrustee
import viewmodels.Tag

class RegistrationProgressSpec extends SpecBase {

  "Trustee section" must {

    "render no tag" when {

      "no trustees in mongo" in {

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

}
