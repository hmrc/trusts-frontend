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

package controllers.actions

import base.RegistrationSpecBase
import connector.SubmissionDraftConnector
import controllers.Assets._
import controllers.actions.register.RegistrationDataRetrievalActionImpl
import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class RegistrationDataRetrievalActionSpec extends RegistrationSpecBase with MockitoSugar with ScalaFutures {

  class Harness(registrationsRepository: RegistrationsRepository,
                submissionDraftConnector: SubmissionDraftConnector) extends RegistrationDataRetrievalActionImpl(registrationsRepository, submissionDraftConnector) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalRegistrationDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "set userAnswers to 'None' in the request" in {

        val registrationsRepository = mock[RegistrationsRepository]
        val submissionDraftConnector = mock[SubmissionDraftConnector]

        when(registrationsRepository.getMostRecentDraftId()(any())) thenReturn Future(None)

        val action = new Harness(registrationsRepository, submissionDraftConnector)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Individual, Enrolments(Set.empty[Enrolment])))

        whenReady(futureResult) { result =>
          result.userAnswers.isEmpty mustBe true
        }
      }
    }

    "there is data in the cache" must {

      "build a userAnswers object and add it to the request" when {

        "agent user" in {

          val registrationsRepository = mock[RegistrationsRepository]
          val submissionDraftConnector = mock[SubmissionDraftConnector]

          when(registrationsRepository.getMostRecentDraftId()(any())) thenReturn Future(Some(fakeDraftId))
          when(registrationsRepository.get(any())(any())) thenReturn Future(Some(emptyUserAnswers))
          when(submissionDraftConnector.adjustDraft(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val action = new Harness(registrationsRepository, submissionDraftConnector)

          val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Agent, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result.userAnswers.isDefined mustBe true
          }

          verify(submissionDraftConnector, times(0)).adjustDraft(eqTo(fakeDraftId))(any(), any())
          verify(registrationsRepository).get(eqTo(fakeDraftId))(any())
        }

        "org user" in {

          val registrationsRepository = mock[RegistrationsRepository]
          val submissionDraftConnector = mock[SubmissionDraftConnector]

          when(registrationsRepository.getMostRecentDraftId()(any())) thenReturn Future(Some(fakeDraftId))
          when(registrationsRepository.get(any())(any())) thenReturn Future(Some(emptyUserAnswers))
          when(submissionDraftConnector.adjustDraft(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val action = new Harness(registrationsRepository, submissionDraftConnector)

          val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result.userAnswers.isDefined mustBe true
          }

          verify(submissionDraftConnector).adjustDraft(eqTo(fakeDraftId))(any(), any())
          verify(registrationsRepository).get(eqTo(fakeDraftId))(any())
        }
      }

      "set userAnswers to 'None' because 'get' query returns 'None'" when {

        "agent user" in {

          val registrationsRepository = mock[RegistrationsRepository]
          val submissionDraftConnector = mock[SubmissionDraftConnector]

          when(registrationsRepository.getMostRecentDraftId()(any())) thenReturn Future(Some(fakeDraftId))
          when(registrationsRepository.get(any())(any())) thenReturn Future(None)
          when(submissionDraftConnector.adjustDraft(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val action = new Harness(registrationsRepository, submissionDraftConnector)

          val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Agent, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result.userAnswers.isEmpty mustBe true
          }

          verify(submissionDraftConnector, times(0)).adjustDraft(eqTo(fakeDraftId))(any(), any())
          verify(registrationsRepository).get(eqTo(fakeDraftId))(any())
        }

        "org user" in {

          val registrationsRepository = mock[RegistrationsRepository]
          val submissionDraftConnector = mock[SubmissionDraftConnector]

          when(registrationsRepository.getMostRecentDraftId()(any())) thenReturn Future(Some(fakeDraftId))
          when(registrationsRepository.get(any())(any())) thenReturn Future(None)
          when(submissionDraftConnector.adjustDraft(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val action = new Harness(registrationsRepository, submissionDraftConnector)

          val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "internalId", AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result.userAnswers.isEmpty mustBe true
          }

          verify(submissionDraftConnector).adjustDraft(eqTo(fakeDraftId))(any(), any())
          verify(registrationsRepository).get(eqTo(fakeDraftId))(any())
        }
      }
    }
  }
}
