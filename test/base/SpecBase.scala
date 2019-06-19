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
import models.UserAnswers
import org.scalatest.{BeforeAndAfter, TestSuite, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.mvc.PlayBodyParsers
import play.api.test.FakeRequest
import repositories.SessionRepository
import services.{CreateDraftRegistrationService, SubmissionService}
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.TestUserAnswers

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

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None,
                                   affinityGroup: AffinityGroup = AffinityGroup.Organisation): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].toInstance(new FakeIdentifierAction(affinityGroup)(injectedParsers)),
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[DraftIdRetrievalActionProvider].toInstance(new FakeDraftIdRetrievalActionProvider("draftId",userAnswers, mockedSessionRepository)),
        bind[SessionRepository].toInstance(mockedSessionRepository),
        bind[SubmissionService].toInstance(mockSubmissionService),
        bind[CreateDraftRegistrationService].toInstance(mockCreateDraftRegistrationService)
      )

}

trait SpecBase extends PlaySpec with SpecBaseHelpers