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

import base.{Fixtures, RegistrationSpecBase}
import forms.DeclarationFormProvider
import models.core.UserAnswers
import models.core.http.RegistrationTRNResponse
import models.core.http.TrustResponse._
import models.core.pages.{Declaration, FullName}
import models.requests.RegistrationDataRequest
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import pages.register.{DeclarationPage, RegistrationProgress}
import play.api.data.Form
import play.api.http.Status.OK
import play.api.inject
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._

import ch.qos.logback.classic.Level
import play.api.Logger
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing
import views.html.register.DeclarationView

import scala.concurrent.Future

class DeclarationControllerSpec extends RegistrationSpecBase with Fixtures with LogCapturing {

  val formProvider            = new DeclarationFormProvider()
  val form: Form[Declaration] = formProvider()

  lazy val declarationRoute: String = routes.DeclarationController.onPageLoad(fakeDraftId).url

  before {
    reset(mockSubmissionService)
  }

  val validAnswer: Declaration = Declaration(FullName("First", None, "Last"), Some("email@email.com"))

  implicit val hc: HeaderCarrier = HeaderCarrier()

  when(registrationsRepository.getDraftSettlors(any())(any()))
    .thenReturn(Future.successful(validGetDraftSettlorsJson))

  when(registrationsRepository.getDraftSettlors(any())(any()))
    .thenReturn(Future.successful(validGetDraftSettlorsJson))

  when(registrationsRepository.getRegistrationPieces(any())(any()))
    .thenReturn(Future.successful(jsonReturnedByGetRequestPieces))

  "Declaration Controller" must {

    "redirect when registration is not complete" in {
      val mockRegistrationProgress = mock[RegistrationProgress]

      when(mockRegistrationProgress.isTaskListComplete(any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(false))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent)
        .overrides(inject.bind[RegistrationProgress].toInstance(mockRegistrationProgress))
        .build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "return OK and the correct view for a GET for Organisation user" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Organisation).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeclarationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, AffinityGroup.Organisation)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET for Agent" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeclarationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, AffinityGroup.Agent)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(DeclarationPage, validAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val view = application.injector.instanceOf[DeclarationView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, AffinityGroup.Agent)(request, messages).toString

      application.stop()
    }

    "redirect to the confirmation page when valid data is submitted and registration submitted successfully " in {

      when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any()))
        .thenReturn(Future.successful(RegistrationTRNResponse("xTRN12456")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      when(
        registrationsRepository.getRegistrationPieces(eqTo("id"))(any())
      ).thenReturn(Future.successful(jsonReturnedByGetRequestPieces))

      val request = FakeRequest(POST, declarationRoute)
        .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ConfirmationController.onPageLoad(fakeDraftId).url
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
      verify(registrationsRepository, times(0)).setDraftSettlors(eqTo("removedAliveAtRegistration"), any())(any())
      application.stop()
    }

    "redirect to the task list page when valid data is submitted and submission service can not register successfully" in {

      when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any()))
        .thenReturn(Future.failed(UnableToRegister()))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(POST, declarationRoute)
        .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
      verify(registrationsRepository, times(0)).setDraftSettlors(eqTo("removedAliveAtRegistration"), any())(any())
      application.stop()
    }

    "redirect to the task list page when valid data is submitted and submission service returns an unexpected response" in {

      when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any()))
        .thenReturn(Future.successful(InternalServerError))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(POST, declarationRoute)
        .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
      verify(registrationsRepository, times(0)).setDraftSettlors(eqTo("removedAliveAtRegistration"), any())(any())
      application.stop()
    }

    "redirect to the already registered page when valid data is submitted and trust is already registered" in {

      when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any()))
        .thenReturn(Future.successful(AlreadyRegistered))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(POST, declarationRoute)
        .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.UTRSentByPostController.onPageLoad().url
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
      verify(registrationsRepository, times(0)).setDraftSettlors(eqTo("removedAliveAtRegistration"), any())(any())
      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(POST, declarationRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DeclarationView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, AffinityGroup.Agent)(request, messages).toString

      application.stop()
    }

    "redirect to Page Not Found for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.PageNotFoundController.onPageLoad().url

      application.stop()
    }

    "redirect to Page Not Found for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(POST, declarationRoute)
        .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.PageNotFoundController.onPageLoad().url

      application.stop()
    }

    "re-throw the exception when a non fatal exception is thrown during submission" in {

      val nonFatalException = new RuntimeException("Error")

      when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any()))
        .thenReturn(Future.failed(nonFatalException))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(POST, declarationRoute)
        .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

      val result = route(application, request).value

      val thrown = intercept[RuntimeException] {
        await(result)
      }

      thrown.getMessage mustEqual "Error"
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
      verify(registrationsRepository, times(0)).setDraftSettlors(eqTo("removedAliveAtRegistration"), any())(any())

      application.stop()
    }

    Seq(
      ("not", BAD_REQUEST),
      ("is", OK)
    ).foreach { case (outcome, setDraftSettlorsHttpResponse) =>
      s"redirect to the confirmation page when valid data is submitted, aliveAtRegistration field $outcome removed successfully " +
        "and registration submitted successfully " in
        {

          val draftId = s"${outcome}RemovedAliveAtRegistrationUnsuccessful"

          when(
            registrationsRepository.getRegistrationPieces(eqTo(draftId))(any())
          ).thenReturn(Future.successful(jsonReturnedByGetRequestPieces))

          when(
            mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
          ).thenReturn(Future.successful(RegistrationTRNResponse("xTRN12456")))

          when(
            registrationsRepository.setDraftSettlors(eqTo(draftId), any())(any())
          ).thenReturn(Future.successful(HttpResponse(setDraftSettlorsHttpResponse, "")))

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

          val removedAliveAtRegistrationDeclarationRoute: String =
            routes.DeclarationController.onPageLoad(draftId).url

          val request = FakeRequest(POST, removedAliveAtRegistrationDeclarationRoute)
            .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.ConfirmationController.onPageLoad(fakeDraftId).url
          verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
          verify(registrationsRepository, times(1)).setDraftSettlors(eqTo(draftId), any())(any())
          application.stop()
        }
    }

    "redirect to the missing mandatory settlor information page if settlor data is not present at declaration" in {
      val userAnswers = emptyUserAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val jsonWithoutMandatorySettlorInfo: JsValue = Json.parse(
        """
          |{
          |  "_id": "193af51f-a9b1-4aec-9932-a7a32c33dc77",
          |  "data": {
          |    "nothingToSeeHere": ""
          |  },
          |  "internalId": "Int-2b56bf2a-0d8e-4aec-ba40-a1d88b66013f",
          |  "isTaxable": false
          |}
          |""".stripMargin
      )

      when(registrationsRepository.getDraftSettlors(any())(any()))
        .thenReturn(Future.successful(jsonWithoutMandatorySettlorInfo))

      when(registrationsRepository.getRegistrationPieces(any())(any()))
        .thenReturn(Future.successful(Json.parse("{}").as[JsObject]))

      when(
        mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
      ).thenReturn(Future.successful(RegistrationTRNResponse("xTRN12456")))

      val expectedMissingSettlorInfo =
        "registration: no settlor information provided. " +
          "Trust should have either a deceased settlor, an individual settlor or a company settlor, " +
          "answer section: no settlors section found"

      withCaptureOfLoggingFrom(Logger(classOf[DeclarationController])) { logEvents =>
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest(POST, routes.DeclarationController.onPageLoad("id").url)
            .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.MissingSettlorController.onPageLoad("id").url

        verify(mockAuditService, times(1))
          .auditRegistrationWithMissingSettlorInfo(
            eqTo(userAnswers),
            eqTo(expectedMissingSettlorInfo)
          )(any[RegistrationDataRequest[_]], any[HeaderCarrier])

        logEvents.filter(_.getLevel == Level.ERROR).exists { event =>
            event.getFormattedMessage.contains(
              s"Trust registration proceeding with missing settlor information: $expectedMissingSettlorInfo"
            )
        } mustBe true
      }
    }



  }

}
