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

package navigation.navigators

import base.SpecBase
import controllers.actions.{DataRetrievalAction, FakeDataRetrievalAction}
import generators.Generators
import navigation.Navigator
import org.scalatest.prop.PropertyChecks
import org.scalacheck.Arbitrary.arbitrary
import pages._
import models.{NormalMode, UserAnswers}
import controllers.routes
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.AffinityGroup

trait MatchingRoutes {

  self: PropertyChecks with Generators with SpecBase =>

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
                  .mustBe(routes.AgentInternalReferenceController.onPageLoad(NormalMode, fakeDraftId))
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

      "go to CannotMakeChanges from TrustHaveAUTR when user answers yes when variations is off" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrustRegisteredOnlinePage, true).success.value
              .set(TrustHaveAUTRPage, true).success.value

            navigator.nextPage(TrustHaveAUTRPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.CannotMakeChangesController.onPageLoad())
        }
      }
      "go to WhatIsTheUtrVariations from TrustHaveAUTR when user answers yes when variations is on" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers
              .set(TrustRegisteredOnlinePage, true).success.value
              .set(TrustHaveAUTRPage, true).success.value

            val app = new GuiceApplicationBuilder().overrides(
              bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(Some(answers)))
            ).configure(("microservice.services.features.variations", true)).build()

            val nav = app.injector.instanceOf[Navigator]

            nav.nextPage(TrustHaveAUTRPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.WhatIsTheUTRVariationsController.onPageLoad(NormalMode, fakeDraftId))
        }
      }
    }
  }

}
