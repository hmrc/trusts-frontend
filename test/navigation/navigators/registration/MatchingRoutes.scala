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

package navigation.navigators.registration

import base.RegistrationSpecBase
import config.FrontendAppConfig
import controllers.actions.FakeRegistrationDataRetrievalAction
import controllers.actions.register.RegistrationDataRetrievalAction
import controllers.register.routes
import generators.Generators
import models.NormalMode
import models.core.UserAnswers
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import pages.register.{TrustHaveAUTRPage, TrustNamePage, TrustRegisteredOnlinePage, WhatIsTheUTRPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.AffinityGroup
import org.mockito.Mockito.when

trait MatchingRoutes {

  self: PropertyChecks with Generators with RegistrationSpecBase =>

  def matchingRoutes()(implicit navigator : Navigator) = {

    "go to TrustHaveAUTR from TrustRegisteredOnline page" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(TrustRegisteredOnlinePage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.TrustHaveAUTRController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "navigate when trust is not registered online" when {

      "the user has a UTR for the trust" when {

        "go to WhatIsTrustsUTR from TrustHaveAUTR when user answers yes" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(TrustRegisteredOnlinePage, false).success.value
                .set(TrustHaveAUTRPage, true).success.value

              navigator.nextPage(TrustHaveAUTRPage, NormalMode, fakeDraftId)(answers)
                .mustBe(routes.WhatIsTheUTRController.onPageLoad(NormalMode, fakeDraftId))
          }
        }

        "go to TrustName from WhatIsTheUTR" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(WhatIsTheUTRPage, NormalMode, fakeDraftId)(userAnswers)
                .mustBe(routes.TrustNameController.onPageLoad(NormalMode, fakeDraftId))
          }
        }

        "go to PostcodeForTheTrust from TrustName" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(TrustHaveAUTRPage, true).success.value

              navigator.nextPage(TrustNamePage, NormalMode, fakeDraftId)(answers)
                .mustBe(routes.PostcodeForTheTrustController.onPageLoad(NormalMode, fakeDraftId))
          }
        }

      }

      "the user does not have a UTR for the trust" when {

        "user is an agent" must {

          "go to AgentInternalReference from TrustHaveAUTR when user answers no" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>

                val answers = userAnswers.set(TrustRegisteredOnlinePage, false).success.value
                  .set(TrustHaveAUTRPage, false).success.value

                navigator.nextPage(TrustHaveAUTRPage, NormalMode, fakeDraftId, AffinityGroup.Agent)(answers)
                  .mustBe(controllers.register.agents.routes.AgentInternalReferenceController.onPageLoad(NormalMode, fakeDraftId))
            }
          }
        }

        "user is an organisation" must {

          "go to TaskList from TrustHaveAUTR when user answers no" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>

                val answers = userAnswers.set(TrustRegisteredOnlinePage, false).success.value
                  .set(TrustHaveAUTRPage, false).success.value

                navigator.nextPage(TrustHaveAUTRPage, NormalMode, fakeDraftId, AffinityGroup.Organisation)(answers)
                  .mustBe(routes.TaskListController.onPageLoad(fakeDraftId))
            }
          }

        }

      }

    }

    "navigate when trust is registered online" when {

      "go to UTRSentByPost from TrustHaveAUTR when user answers no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrustRegisteredOnlinePage, true).success.value
              .set(TrustHaveAUTRPage, false).success.value

            navigator.nextPage(TrustHaveAUTRPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.UTRSentByPostController.onPageLoad())
        }
      }

      "go to CannotMakeChanges from TrustHaveAUTR when user answers yes when playback is off" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers
              .set(TrustRegisteredOnlinePage, true).success.value
              .set(TrustHaveAUTRPage, true).success.value

            val feAppConfig = mock[FrontendAppConfig]
            when(feAppConfig.playbackEnabled).thenReturn(false)
            val nav = new Navigator(feAppConfig)

            nav.nextPage(TrustHaveAUTRPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.CannotMakeChangesController.onPageLoad())

        }
      }
      "go to Maintain a trust service from TrustHaveAUTR when user answers yes when playback is on" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers
              .set(TrustRegisteredOnlinePage, true).success.value
              .set(TrustHaveAUTRPage, true).success.value

            val feAppConfig = mock[FrontendAppConfig]

            when(feAppConfig.playbackEnabled).thenReturn(true)

            val nav = new Navigator(feAppConfig)

            val url = nav.nextPage(TrustHaveAUTRPage, NormalMode, fakeDraftId)(answers).url

            url mustBe feAppConfig.maintainATrustFrontendUrl
        }
      }
    }
  }

}
