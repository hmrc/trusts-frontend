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

import config.FrontendAppConfig
import controllers.actions._
import models.{RegistrationStatus, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalatest.{BeforeAndAfter, TestSuite, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.mvc.{BodyParsers, PlayBodyParsers}
import play.api.test.FakeRequest
import repositories.RegistrationsRepository
import services.{CreateDraftRegistrationService, SubmissionService}
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.TestUserAnswers
import utils.annotations.{LivingSettlor, PropertyOrLand}

trait SpecBaseHelpers extends GuiceOneAppPerSuite with TryValues with Mocked with BeforeAndAfter {
  this: TestSuite =>

  val userAnswersId = TestUserAnswers.draftId

  def emptyUserAnswers = TestUserAnswers.emptyUserAnswers

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest = FakeRequest("", "")

  def fakeDraftId: String = TestUserAnswers.draftId

  def injectedParsers = injector.instanceOf[PlayBodyParsers]

  val injectedDefaultParsers =injector.instanceOf[BodyParsers.Default]

  def trustsAuth = injector.instanceOf[TrustsAuth]

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  lazy val fakeNavigator = new FakeNavigator(frontendAppConfig)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None,
                                   affinityGroup: AffinityGroup = AffinityGroup.Organisation,
                                   navigator: Navigator = fakeNavigator
                                  ): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifyForRegistration].toInstance(new FakeIdentifierAction(affinityGroup)(injectedDefaultParsers, trustsAuth)),
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[DraftIdRetrievalActionProvider].toInstance(
          new FakeDraftIdRetrievalActionProvider("draftId", RegistrationStatus.InProgress, userAnswers, registrationsRepository)),
        bind[RegistrationsRepository].toInstance(registrationsRepository),
        bind[SubmissionService].toInstance(mockSubmissionService),
        bind[CreateDraftRegistrationService].toInstance(mockCreateDraftRegistrationService),
        bind[Navigator].toInstance(navigator),
        bind[Navigator].qualifiedWith(classOf[PropertyOrLand]).toInstance(navigator),
        bind[Navigator].qualifiedWith(classOf[LivingSettlor]).toInstance(navigator)
      )

}

trait SpecBase extends PlaySpec with SpecBaseHelpers