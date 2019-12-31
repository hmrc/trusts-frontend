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

package base

import controllers.actions.playback.PlaybackIdentifierAction
import controllers.actions.register._
import controllers.actions.{FakeDraftIdRetrievalActionProvider, _}
import models.core.UserAnswers
import models.registration.pages.RegistrationStatus
import navigation.{FakeNavigator, Navigator}
import org.scalatest.{BeforeAndAfter, TestSuite, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.RegistrationsRepository
import services.{CreateDraftRegistrationService, SubmissionService}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import utils.TestUserAnswers
import utils.annotations.{LivingSettlor, PropertyOrLand}

trait SpecBaseHelpers extends GuiceOneAppPerSuite with TryValues with Mocked with BeforeAndAfter with FakeTrustsApp {
  this: TestSuite =>

  val fakeDraftId = TestUserAnswers.draftId

  def emptyUserAnswers = TestUserAnswers.emptyUserAnswers

  lazy val fakeNavigator = new FakeNavigator(frontendAppConfig)

  private def fakeDraftIdAction(userAnswers: Option[UserAnswers]) = new FakeDraftIdRetrievalActionProvider(
      "draftId",
      RegistrationStatus.InProgress,
      userAnswers,
      registrationsRepository
    )

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None,
                                   affinityGroup: AffinityGroup = AffinityGroup.Organisation,
                                   enrolments: Enrolments = Enrolments(Set.empty[Enrolment]),
                                   navigator: Navigator = fakeNavigator
                                  ): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[RegistrationDataRequiredAction].to[RegistrationDataRequiredActionImpl],
        bind[RegistrationIdentifierAction].toInstance(new FakeIdentifyForRegistration(affinityGroup)(injectedParsers, trustsAuth, enrolments)),
        bind[PlaybackIdentifierAction].toInstance(new FakePlaybackIdentifierAction()),
        bind[RegistrationDataRetrievalAction].toInstance(new FakeRegistrationDataRetrievalAction(userAnswers)),
        bind[DraftIdRetrievalActionProvider].toInstance(fakeDraftIdAction(userAnswers)),
        bind[RegistrationsRepository].toInstance(registrationsRepository),
        bind[SubmissionService].toInstance(mockSubmissionService),
        bind[AffinityGroup].toInstance(Organisation),
        bind[CreateDraftRegistrationService].toInstance(mockCreateDraftRegistrationService),
        bind[Navigator].toInstance(navigator),
        bind[Navigator].qualifiedWith(classOf[PropertyOrLand]).toInstance(navigator),
        bind[Navigator].qualifiedWith(classOf[LivingSettlor]).toInstance(navigator)
      )

}

trait RegistrationSpecBase extends PlaySpec with SpecBaseHelpers