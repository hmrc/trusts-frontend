/*
 * Copyright 2021 HM Revenue & Customs
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

import config.FrontendAppConfig
import controllers.actions.register._
import controllers.actions.{FakeDraftIdRetrievalActionProvider, _}
import models.core.http.{IdentificationOrgType, LeadTrusteeOrgType, LeadTrusteeType}
import models.core.{MatchingAndSuitabilityUserAnswers, TrustsFrontendUserAnswers, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalatest.{BeforeAndAfter, TestSuite, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.{CacheRepository, RegistrationsRepository}
import services.{DraftRegistrationService, SubmissionService, TrustsStoreService}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import utils.TestUserAnswers

trait SpecBaseHelpers extends GuiceOneAppPerSuite with TryValues with Mocked with BeforeAndAfter with FakeTrustsApp {
  this: TestSuite =>

  final val ENGLISH = "en"
  final val WELSH = "cy"

  val fakeDraftId: String = TestUserAnswers.draftId

  def emptyUserAnswers: UserAnswers = TestUserAnswers.emptyUserAnswers
  def emptyMatchingAndSuitabilityUserAnswers: MatchingAndSuitabilityUserAnswers = TestUserAnswers.emptyMatchingAndSuitabilityUserAnswers

  lazy val fakeNavigator = new FakeNavigator(fakeFrontendAppConfig)

  val testLeadTrusteeOrg: LeadTrusteeType = LeadTrusteeType(
    None,
    Some(LeadTrusteeOrgType(
      "Lead Org",
      "07911234567",
      None,
      IdentificationOrgType(Some("utr"), None)))
  )

  def fakeDraftIdAction(userAnswers: Option[TrustsFrontendUserAnswers[_]]) = new FakeDraftIdRetrievalActionProvider(userAnswers)

  protected def applicationBuilder(userAnswers: Option[TrustsFrontendUserAnswers[_]] = None,
                                   affinityGroup: AffinityGroup = AffinityGroup.Organisation,
                                   enrolments: Enrolments = Enrolments(Set.empty[Enrolment]),
                                   navigator: Navigator = fakeNavigator): GuiceApplicationBuilder =

    new GuiceApplicationBuilder()
      .overrides(
        bind[RegistrationDataRequiredAction].to[RegistrationDataRequiredActionImpl],
        bind[RegistrationIdentifierAction].toInstance(new FakeIdentifyForRegistration(affinityGroup, fakeFrontendAppConfig)(injectedParsers, trustsAuth, enrolments)),
        bind[ConfirmationIdentifierAction].toInstance(new FakeIdentifyForConfirmation(affinityGroup, fakeFrontendAppConfig)(injectedParsers, trustsAuth, enrolments)),
        bind[MatchingAndSuitabilityDataRetrievalAction].toInstance(new FakeMatchingAndSuitabilityDataRetrievalAction(userAnswers)),
        bind[RegistrationDataRetrievalAction].toInstance(new FakeRegistrationDataRetrievalAction(userAnswers)),
        bind[DraftIdRetrievalActionProvider].toInstance(fakeDraftIdAction(userAnswers)),
        bind[CacheRepository].toInstance(cacheRepository),
        bind[RegistrationsRepository].toInstance(registrationsRepository),
        bind[SubmissionService].toInstance(mockSubmissionService),
        bind[TrustsStoreService].toInstance(mockTrustsStoreService),
        bind[AffinityGroup].toInstance(Organisation),
        bind[DraftRegistrationService].toInstance(mockCreateDraftRegistrationService),
        bind[Navigator].toInstance(navigator),
        bind[FrontendAppConfig].to(fakeFrontendAppConfig)
      )
      .configure(
        "microservice.services.features.deployment.notification.enabled" -> false
      )

}

trait RegistrationSpecBase extends PlaySpec with SpecBaseHelpers
