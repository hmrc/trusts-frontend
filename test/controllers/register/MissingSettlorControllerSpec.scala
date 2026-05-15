/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.register

import base.RegistrationSpecBase
import handlers.ErrorHandler
import models.core.TrustsFrontendUserAnswers
import models.registration.pages.RegistrationStatus
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import pages.register.{RegistrationProgress, RegistrationTRNPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{RequestHeader, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import views.html.register.MissingSettlorView

import scala.concurrent.Future

class MissingSettlorControllerSpec extends RegistrationSpecBase with BeforeAndAfterEach {

  private val trn = "XATRUST00000001"

  private lazy val missingSettlorRoute: String =
    routes.MissingSettlorController.onPageLoad(fakeDraftId).url

  private val mockRegistrationProgress: RegistrationProgress = mock[RegistrationProgress]

  private val mockErrorHandler: ErrorHandler = mock[ErrorHandler]

  override def beforeEach(): Unit = {
    reset(mockRegistrationProgress, mockErrorHandler)
    when(mockRegistrationProgress.isTaskListComplete(any(), any(), any(), any())(any()))
      .thenReturn(Future.successful(true))
  }

  override protected def applicationBuilder(
    userAnswers: Option[TrustsFrontendUserAnswers[_]],
    affinityGroup: AffinityGroup = AffinityGroup.Organisation,
    enrolments: Enrolments = Enrolments(Set.empty[Enrolment]),
    navigator: Navigator = fakeNavigator
  ): GuiceApplicationBuilder =
    super
      .applicationBuilder(userAnswers, affinityGroup)
      .overrides(
        bind[RegistrationProgress].toInstance(mockRegistrationProgress),
        bind[ErrorHandler].toInstance(mockErrorHandler)
      )

  "MissingSettlorController" must {

    "return OK and the correct view for a GET when the TRN is available" in {

      val userAnswers = emptyUserAnswers
        .set(RegistrationTRNPage, trn)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, missingSettlorRoute)
      val result  = route(application, request).value
      val view    = application.injector.instanceOf[MissingSettlorView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(trn)(request, messages).toString

      application.stop()
    }

    "return InternalServerError when no TRN is available" in {

      when(mockErrorHandler.onServerError(any[RequestHeader], any[Throwable]))
        .thenReturn(Future.successful(Results.InternalServerError("error")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, missingSettlorRoute)
      val result  = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      application.stop()
    }

  }

}
