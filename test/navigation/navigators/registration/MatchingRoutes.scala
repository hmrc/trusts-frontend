/*
 * Copyright 2024 HM Revenue & Customs
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

package navigation.navigators.registration

import base.RegistrationSpecBase
import config.FrontendAppConfig
import controllers.register.routes
import generators.Generators
import models.core.UserAnswers
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register._

trait MatchingRoutes {

  self: ScalaCheckPropertyChecks with Generators with RegistrationSpecBase =>

  def matchingRoutes()(implicit navigator: Navigator): Unit = {

    "TrustRegisteredOnline page -> Yes -> TrustHaveAUTR page" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.set(TrustRegisteredOnlinePage, false).success.value

        navigator
          .nextPage(TrustRegisteredOnlinePage)(answers)
          .mustBe(routes.TrustHaveAUTRController.onPageLoad())
      }

    "TrustRegisteredOnline page -> no -> TrustHaveAUTR page" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.set(TrustRegisteredOnlinePage, true).success.value

        navigator
          .nextPage(TrustRegisteredOnlinePage)(answers)
          .mustBe(routes.WhichIdentifierController.onPageLoad())
      }

    "navigate when trust is not registered online" when {

      "the user has a UTR for the trust" when {

        "go to WhatIsTrustsUTR from TrustHaveAUTR when user answers yes" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers
              .set(TrustRegisteredOnlinePage, false)
              .success
              .value
              .set(TrustHaveAUTRPage, true)
              .success
              .value

            navigator
              .nextPage(TrustHaveAUTRPage)(answers)
              .mustBe(routes.WhatIsTheUTRController.onPageLoad())
          }

        "go to TrustName from WhatIsTheUTR" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            navigator
              .nextPage(WhatIsTheUTRPage)(userAnswers)
              .mustBe(controllers.register.routes.MatchingNameController.onPageLoad())
          }

        "go to TrustRegisteredWithUkAddressYesNo from TrustName" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers.set(TrustHaveAUTRPage, true).success.value

            navigator
              .nextPage(MatchingNamePage)(answers)
              .mustBe(routes.TrustRegisteredWithUkAddressYesNoController.onPageLoad())
          }
      }

      "the user does not have a UTR for the trust" must {

        "go to ExpressTrustYesNo Controller" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers
              .set(TrustRegisteredOnlinePage, false)
              .success
              .value
              .set(TrustHaveAUTRPage, false)
              .success
              .value

            navigator
              .nextPage(TrustHaveAUTRPage)(answers)
              .mustBe(controllers.register.suitability.routes.ExpressTrustYesNoController.onPageLoad())
          }
      }
    }

    "navigate when trust is registered online" when {

      "go to UTRSentByPost from TrustHaveAUTR when user answers no" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val answers = userAnswers
            .set(TrustRegisteredOnlinePage, true)
            .success
            .value
            .set(TrustHaveAUTRPage, false)
            .success
            .value

          navigator
            .nextPage(TrustHaveAUTRPage)(answers)
            .mustBe(routes.UTRSentByPostController.onPageLoad())
        }

      "go to Maintain a trust service from TrustHaveAUTR when user answers yes" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val answers = userAnswers
            .set(TrustRegisteredOnlinePage, true)
            .success
            .value
            .set(TrustHaveAUTRPage, true)
            .success
            .value

          val feAppConfig = mock[FrontendAppConfig]

          val nav = new Navigator(feAppConfig)

          val url = nav.nextPage(TrustHaveAUTRPage)(answers).url

          url mustBe feAppConfig.maintainATrustFrontendUrl
        }
    }
  }

}
