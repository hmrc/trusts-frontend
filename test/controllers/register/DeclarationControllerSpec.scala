/*
 * Copyright 2025 HM Revenue & Customs
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
import forms.DeclarationFormProvider
import models.core.UserAnswers
import models.core.http.RegistrationTRNResponse
import models.core.http.TrustResponse._
import models.core.pages.{Declaration, FullName}
import models.requests.RegistrationDataRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
import pages.register.{DeclarationPage, RegistrationProgress}
import play.api.data.Form
import play.api.http.Status.OK
import play.api.{Application, inject}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import views.html.register.DeclarationView
import org.mockito.Mockito.{reset, times, verify, when}
import play.api.mvc.{AnyContent, AnyContentAsFormUrlEncoded}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DeclarationControllerSpec extends RegistrationSpecBase {

  val formProvider = new DeclarationFormProvider()
  val form: Form[Declaration] = formProvider()

  lazy val declarationRoute: String = routes.DeclarationController.onPageLoad(fakeDraftId).url

  before {
    reset(mockSubmissionService)
  }

  val validAnswer: Declaration = Declaration(FullName("First", None, "Last"), Some("email@email.com"))

  val jsonReturnedByGetRequestPieces: JsObject = Json.parse(
    """
      |{
      |  "trust/entities/settlors": {
      |    "settlor": [
      |      {
      |        "aliveAtRegistration": false,
      |        "name": {
      |          "firstName": "Mark",
      |          "lastName": "B"
      |        },
      |        "identification": {
      |          "address": {
      |            "line1": "123",
      |            "line2": "Test address",
      |            "postCode": "AB1 1AB",
      |            "country": "GB"
      |          }
      |        },
      |        "countryOfResidence": "GB",
      |        "nationality": "GB"
      |      }
      |    ]
      |  }
      |}
      """.stripMargin
  ).as[JsObject]

  val validGetDraftSettlorsJson: JsValue = Json.parse(
    """
      |{
      |  "_id": "193af51f-a9b1-4aec-9932-a7a32c33dc77",
      |  "data": {
      |    "settlors": {
      |      "setUpByLivingSettlorYesNo": false,
      |      "deceased": {
      |        "name": {
      |          "firstName": "Will",
      |          "middleName": "James",
      |          "lastName": "Graham"
      |        },
      |        "dateOfDeathYesNo": true,
      |        "dateOfDeath": "2017-03-13",
      |        "dateOfBirthYesNo": true,
      |        "settlorsDateOfBirth": "1957-03-13",
      |        "countryOfNationalityYesNo": true,
      |        "countryOfNationalityInTheUkYesNo": false,
      |        "countryOfNationality": "JO",
      |        "countryOfResidenceYesNo": true,
      |        "countryOfResidenceInTheUkYesNo": false,
      |        "countryOfResidence": "LV",
      |        "status": "completed"
      |      }
      |    }
      |  },
      |  "internalId": "Int-2b56bf2a-0d8e-4aec-ba40-a1d88b66013f",
      |  "isTaxable": false
      |}
      |""".stripMargin
  )

  implicit val hc: HeaderCarrier = HeaderCarrier()

  when(registrationsRepository.getDraftSettlors(any())(any()))
    .thenReturn(Future.successful(validGetDraftSettlorsJson))

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
        .set(DeclarationPage, validAnswer).success.value

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

      when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any())).
        thenReturn(Future.successful(RegistrationTRNResponse("xTRN12456")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

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

      when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any())).
        thenReturn(Future.failed(UnableToRegister()))

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

      when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any())).
        thenReturn(Future.successful(AlreadyRegistered))

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

    "redirect to the confirmation page when valid data is submitted, aliveAtRegistration field is removed successfully, " +
      "and registration submitted successfully" in {

      val draftId = "123"
      val (application, request) = setupAliveAtRegistrationTests(draftId, OK)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ConfirmationController.onPageLoad(fakeDraftId).url
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
      verify(registrationsRepository, times(1)).setDraftSettlors(eqTo(draftId), any())(any())
      application.stop()
    }

    "redirect to the confirmation page when valid data is submitted, aliveAtRegistration field not removed successfully, " +
      "and registration submitted successfully" in {

      val draftId = "345"
      val (application, request) = setupAliveAtRegistrationTests(draftId, BAD_REQUEST)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ConfirmationController.onPageLoad(fakeDraftId).url
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any(), any[HeaderCarrier], any())
      verify(registrationsRepository, times(1)).setDraftSettlors(eqTo(draftId), any())(any())
      application.stop()
    }

    "throw an UnableToRegister exception when .getExpectedSettlorData called given registrationsRepository.getDraftSettlors returns no settlor data" in {
      val userAnswers = emptyUserAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()
      val controller = application.injector.instanceOf[DeclarationController]

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

      implicit val request: RegistrationDataRequest[AnyContent] =
        RegistrationDataRequest(fakeRequest, "internalId", "sessionId", userAnswers, AffinityGroup.Agent, Enrolments(Set()))

      intercept[UnableToRegister] {
        Await.result(controller.getExpectedSettlorData("draftId"), Duration.Inf)
      }

      verify(mockAuditService, times(1))
        .auditRegistrationPreparationFailed(userAnswers, "Error attempting to register trust without mandatory settlor information")

    }

    "return the expected data when .getExpectedSettlorData called given settlor data exists" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()
      val controller = application.injector.instanceOf[DeclarationController]

      implicit val request: RegistrationDataRequest[AnyContent] =
        RegistrationDataRequest(fakeRequest, "internalId", "sessionId", emptyUserAnswers, AffinityGroup.Organisation, Enrolments(Set()))

      when(registrationsRepository.getDraftSettlors(any())(any()))
        .thenReturn(Future.successful(validGetDraftSettlorsJson))

      Await.result(controller.getExpectedSettlorData("draftId"), Duration.Inf) mustEqual validGetDraftSettlorsJson
    }
  }

  def setupAliveAtRegistrationTests(draftId: String, setDraftSettlorsResponseStatusCode: Int)
  : (Application, FakeRequest[AnyContentAsFormUrlEncoded]) = {
    when(registrationsRepository.getDraftSettlors(eqTo(draftId))(any()))
      .thenReturn(Future.successful(validGetDraftSettlorsJson))

    when(registrationsRepository.getRegistrationPieces(eqTo(draftId))(any()))
      .thenReturn(Future.successful(jsonReturnedByGetRequestPieces))

    when(mockSubmissionService.submit(any[UserAnswers])(any(), any[HeaderCarrier], any()))
      .thenReturn(Future.successful(RegistrationTRNResponse("xTRN12456")))

    when(registrationsRepository.setDraftSettlors(eqTo(draftId), any())(any()))
      .thenReturn(Future.successful(HttpResponse(setDraftSettlorsResponseStatusCode, "")))

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

    val removedAliveAtRegistrationDeclarationRoute: String =
      routes.DeclarationController.onPageLoad(draftId).url

    val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, removedAliveAtRegistrationDeclarationRoute)
      .withFormUrlEncodedBody(("firstName", validAnswer.name.firstName), ("lastName", validAnswer.name.lastName))

    (application, request)
  }

}
