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

package controllers.register.suitability

import base.RegistrationSpecBase
import connector.TrustsAuthConnector
import forms.AccessCodeFormProvider
import models.{TrustsAuthAllowed, TrustsAuthDenied, TrustsAuthInternalServerError}
import navigation.registration.TaskListNavigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.register.suitability.NonTaxableTrustRegistrationAccessCodeView

import scala.concurrent.Future

class NonTaxableTrustRegistrationAccessCodeControllerSpec extends RegistrationSpecBase with ScalaCheckPropertyChecks {

  private lazy val nonTaxableTrustRegistrationAccessCodeRoute: String =
    routes.NonTaxableTrustRegistrationAccessCodeController.onPageLoad(fakeDraftId).url

  private val form = new AccessCodeFormProvider()()

  private val navigator: TaskListNavigator = mock[TaskListNavigator]

  private val mockTrustsAuthConnector = mock[TrustsAuthConnector]

  private val validAnswer: String = "123456"

  "NonTaxableTrustRegistrationAccessCodeController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, nonTaxableTrustRegistrationAccessCodeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NonTaxableTrustRegistrationAccessCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {

      "access code is authorised" when {

        "agent user" in {

          val redirectUrl = "redirect-url"

          when(mockTrustsAuthConnector.authoriseAccessCode(any(), any())(any(), any()))
            .thenReturn(Future.successful(TrustsAuthAllowed()))

          when(navigator.agentDetailsJourneyUrl(any())).thenReturn(redirectUrl)

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = AffinityGroup.Agent)
            .overrides(
              bind[TrustsAuthConnector].toInstance(mockTrustsAuthConnector),
              bind[TaskListNavigator].toInstance(navigator)
            )
            .build()

          val request = FakeRequest(POST, nonTaxableTrustRegistrationAccessCodeRoute)
            .withFormUrlEncodedBody(("value", validAnswer))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual redirectUrl

          application.stop()
        }

        "non-agent user" in {

          when(mockTrustsAuthConnector.authoriseAccessCode(any(), any())(any(), any()))
            .thenReturn(Future.successful(TrustsAuthAllowed()))

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = AffinityGroup.Organisation)
            .overrides(bind[TrustsAuthConnector].toInstance(mockTrustsAuthConnector))
            .build()

          val request = FakeRequest(POST, nonTaxableTrustRegistrationAccessCodeRoute)
            .withFormUrlEncodedBody(("value", validAnswer))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.register.routes.TaskListController.onPageLoad(fakeDraftId).url

          application.stop()
        }
      }

      "access code is not authorised" in {

        when(mockTrustsAuthConnector.authoriseAccessCode(any(), any())(any(), any()))
          .thenReturn(Future.successful(TrustsAuthDenied("redirectUrl")))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustsAuthConnector].toInstance(mockTrustsAuthConnector))
          .build()

        val request = FakeRequest(POST, nonTaxableTrustRegistrationAccessCodeRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        application.stop()
      }

      "error authorising access code" in {

        when(mockTrustsAuthConnector.authoriseAccessCode(any(), any())(any(), any()))
          .thenReturn(Future.successful(TrustsAuthInternalServerError))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustsAuthConnector].toInstance(mockTrustsAuthConnector))
          .build()

        val request = FakeRequest(POST, nonTaxableTrustRegistrationAccessCodeRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }
}
